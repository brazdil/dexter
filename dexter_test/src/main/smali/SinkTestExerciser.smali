.class public LSinkTestExerciser;
.super LTestExerciser;

# instance fields
.field private final test:LSinkTest;


# direct methods
.method public constructor <init>(LSinkTest;)V
    .registers 2

    invoke-direct {p0}, LTestExerciser;-><init>()V
    iput-object p1, p0, LSinkTestExerciser;->test:LSinkTest;
    return-void

.end method


# virtual methods
.method public getTest()LTest;
    .registers 2

    iget-object v0, p0, LSinkTestExerciser;->test:LSinkTest;
    return-object v0

.end method

.method public run()Z
    .registers 4

    # get test
    iget-object v0, p0, LSinkTestExerciser;->test:LSinkTest;

    # get context
    invoke-static {}, LTestList;->getContext()Landroid/content/Context;
    move-result v1

    # generate and taint argument
    invoke-interface {v0}, LSinkTest;->arg()Ljava/lang/Object;
    move-result-object v2

    # execute test (untainted)
    :try_start1
    invoke-interface {v0, v2, v1}, LSinkTest;->leak(Ljava/lang/Object;Landroid/content/Context;)V
    :try_end1
    .catch LLeakageException; {:try_start1 .. :try_end1} :handler1

    # no exception => good... now try to taint the argument
    invoke-interface {v0}, LSinkTest;->arg()Ljava/lang/Object;
    move-result-object v2
    invoke-static {v2}, LTaintUtils;->taint(Ljava/lang/Object;)Ljava/lang/Object;
    move-result-object v2

    # execute test (tainted)
    :try_start2
    invoke-interface {v0, v2, v1}, LSinkTest;->leak(Ljava/lang/Object;Landroid/content/Context;)V
    :try_end2
    .catch LLeakageException; {:try_start2 .. :try_end2} :handler2

    # no or wrong exception => return false
    const/4 v0, 0x0
    return v0

    # LeakageException when untainted => return false
    :handler1
    move-exception v0
    const/4 v0, 0x0
    return v0

    # LeakageException when tainted => return true
    :handler2
    move-exception v0
    const/4 v0, 0x1
    return v0

.end method
