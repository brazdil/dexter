.class public LSourceTestExerciser;
.super LTestExerciser;
.source "SourceTestExerciser.java"


# instance fields
.field private final test:LSourceTest;


# direct methods
.method public constructor <init>(LSourceTest;)V
    .registers 2
    .parameter "test"

    .prologue
    .line 10
    invoke-direct {p0}, LTestExerciser;-><init>()V

    .line 11
    iput-object p1, p0, LSourceTestExerciser;->test:LSourceTest;

    .line 12
    return-void
.end method


# virtual methods
.method public getTest()LTest;
    .registers 2

    .prologue
    .line 16
    iget-object v0, p0, LSourceTestExerciser;->test:LSourceTest;

    return-object v0
.end method

.method public run()Z
    .registers 2

    .prologue
    .line 21
    iget-object v0, p0, LSourceTestExerciser;->test:LSourceTest;

    invoke-interface {v0}, LSourceTest;->generate()I

    move-result v0

    invoke-static {v0}, LTaintUtils;->isTainted(I)Z

    move-result v0

    return v0
.end method
