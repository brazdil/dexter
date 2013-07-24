.class public Luk/ac/cam/db538/dexter/tests/PropagationTestExerciser;
.super Luk/ac/cam/db538/dexter/tests/TestExerciser;
.source "PropagationTestExerciser.java"


# instance fields
.field private final test:Luk/ac/cam/db538/dexter/tests/PropagationTest;


# direct methods
.method public constructor <init>(Luk/ac/cam/db538/dexter/tests/PropagationTest;)V
    .registers 2
    .parameter "test"

    .prologue
    .line 10
    invoke-direct {p0}, Luk/ac/cam/db538/dexter/tests/TestExerciser;-><init>()V

    .line 11
    iput-object p1, p0, Luk/ac/cam/db538/dexter/tests/PropagationTestExerciser;->test:Luk/ac/cam/db538/dexter/tests/PropagationTest;

    .line 12
    return-void
.end method


# virtual methods
.method public getTest()Luk/ac/cam/db538/dexter/tests/Test;
    .registers 2

    .prologue
    .line 16
    iget-object v0, p0, Luk/ac/cam/db538/dexter/tests/PropagationTestExerciser;->test:Luk/ac/cam/db538/dexter/tests/PropagationTest;

    return-object v0
.end method

.method public run()Z
    .registers 6

    .prologue
    .line 21
    const/4 v0, 0x1

    .line 22
    .local v0, argNormal:I
    iget-object v4, p0, Luk/ac/cam/db538/dexter/tests/PropagationTestExerciser;->test:Luk/ac/cam/db538/dexter/tests/PropagationTest;

    invoke-interface {v4, v0}, Luk/ac/cam/db538/dexter/tests/PropagationTest;->propagate(I)I

    move-result v2

    .line 24
    .local v2, resNormal:I
    new-instance v4, Luk/ac/cam/db538/dexter/tests/Test_Const;

    invoke-direct {v4}, Luk/ac/cam/db538/dexter/tests/Test_Const;-><init>()V

    invoke-virtual {v4}, Luk/ac/cam/db538/dexter/tests/Test_Const;->generate()I

    move-result v1

    .line 25
    .local v1, argTainted:I
    iget-object v4, p0, Luk/ac/cam/db538/dexter/tests/PropagationTestExerciser;->test:Luk/ac/cam/db538/dexter/tests/PropagationTest;

    invoke-interface {v4, v1}, Luk/ac/cam/db538/dexter/tests/PropagationTest;->propagate(I)I

    move-result v3

    .line 27
    .local v3, resTainted:I
    invoke-static {v2}, Luk/ac/cam/db538/dexter/tests/TaintChecker;->isTainted(I)Z

    move-result v4

    if-nez v4, :cond_24

    invoke-static {v3}, Luk/ac/cam/db538/dexter/tests/TaintChecker;->isTainted(I)Z

    move-result v4

    if-eqz v4, :cond_24

    const/4 v4, 0x1

    :goto_23
    return v4

    :cond_24
    const/4 v4, 0x0

    goto :goto_23
.end method
