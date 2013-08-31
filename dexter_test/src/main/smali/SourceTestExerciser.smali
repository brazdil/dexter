.class public LSourceTestExerciser;
.super LTestExerciser;

# instance fields
.field private final test:LSourceTest;


# direct methods
.method public constructor <init>(LSourceTest;)V
    .registers 2

    invoke-direct {p0}, LTestExerciser;-><init>()V
    iput-object p1, p0, LSourceTestExerciser;->test:LSourceTest;
    return-void

.end method


# virtual methods
.method public getTest()LTest;
    .registers 2

    iget-object v0, p0, LSourceTestExerciser;->test:LSourceTest;
    return-object v0

.end method

.method public run()Z
    .registers 4

    iget-object v0, p0, LSourceTestExerciser;->test:LSourceTest;

    invoke-static {}, LTestList;->getContext()Landroid/content/Context;
    move-result v1

    invoke-interface {v0, v1}, LSourceTest;->generate(Landroid/content/Context;)Ljava/lang/Object;
    move-result-object v2

    invoke-static {v2}, LTaintUtils;->isTainted(Ljava/lang/Object;)Z
    move-result v0

    return v0

.end method
