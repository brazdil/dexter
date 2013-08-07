.class public Luk/ac/cam/db538/dexter/tests/NoPropagationTestExerciser;
.super Luk/ac/cam/db538/dexter/tests/TestExerciser;


# instance fields
.field private final test:Luk/ac/cam/db538/dexter/tests/PropagationTest;


# direct methods
.method public constructor <init>(Luk/ac/cam/db538/dexter/tests/PropagationTest;)V
    .registers 2

    invoke-direct {p0}, Luk/ac/cam/db538/dexter/tests/TestExerciser;-><init>()V
    iput-object p1, p0, Luk/ac/cam/db538/dexter/tests/NoPropagationTestExerciser;->test:Luk/ac/cam/db538/dexter/tests/PropagationTest;
    return-void

.end method


# virtual methods
.method public getTest()Luk/ac/cam/db538/dexter/tests/Test;
    .registers 2

    iget-object v0, p0, Luk/ac/cam/db538/dexter/tests/NoPropagationTestExerciser;->test:Luk/ac/cam/db538/dexter/tests/PropagationTest;
    return-object v0

.end method

.method public run()Z
    .registers 6

    # First run with an untainted argument

    const/4 v0, 0x1
    iget-object v1, p0, Luk/ac/cam/db538/dexter/tests/NoPropagationTestExerciser;->test:Luk/ac/cam/db538/dexter/tests/PropagationTest;
    invoke-interface {v1, v0}, Luk/ac/cam/db538/dexter/tests/PropagationTest;->propagate(I)I
    move-result v2

    # ... and check taint of the result

    invoke-static {v2}, Luk/ac/cam/db538/dexter/tests/TaintChecker;->isTainted(I)Z
    move-result v2

    # Now generate a tainted constant

    new-instance v4, Luk/ac/cam/db538/dexter/tests/Test_Const;
    invoke-direct {v4}, Luk/ac/cam/db538/dexter/tests/Test_Const;-><init>()V
    invoke-virtual {v4}, Luk/ac/cam/db538/dexter/tests/Test_Const;->generate()I
    move-result v0

    # ... run again

    iget-object v1, p0, Luk/ac/cam/db538/dexter/tests/NoPropagationTestExerciser;->test:Luk/ac/cam/db538/dexter/tests/PropagationTest;
    invoke-interface {v1, v0}, Luk/ac/cam/db538/dexter/tests/PropagationTest;->propagate(I)I
    move-result v3

    # ... and again check the taint of the outcome

    invoke-static {v3}, Luk/ac/cam/db538/dexter/tests/TaintChecker;->isTainted(I)Z
    move-result v3

    # Need: v2 == false && v3 == false

    if-nez v2, :return_false
    if-nez v3, :return_false

    # All fine, return true

    const/4 v0, 0x1
    return v0

    # Something not fine, return false

    :return_false
    const/4 v0, 0x0
    return v0

    
.end method
