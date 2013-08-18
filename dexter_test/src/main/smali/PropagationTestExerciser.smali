.class public Luk/ac/cam/db538/dexter/tests/PropagationTestExerciser;
.super Luk/ac/cam/db538/dexter/tests/TestExerciser;
.source "PropagationTestExerciser.java"


.field private final test:Luk/ac/cam/db538/dexter/tests/PropagationTest;
.field private final shouldPropagate:Z

.field private static final RAND:Ljava/util/Random;

.method static constructor <clinit>()V
    .registers 1

    new-instance v0, Ljava/util/Random;
    invoke-direct {v0}, Ljava/util/Random;-><init>()V
    sput-object v0, Luk/ac/cam/db538/dexter/tests/PropagationTestExerciser;->RAND:Ljava/util/Random;

    return-void

.end method

# direct methods
.method public constructor <init>(Luk/ac/cam/db538/dexter/tests/PropagationTest;Z)V
    .registers 4

    invoke-direct {p0}, Luk/ac/cam/db538/dexter/tests/TestExerciser;-><init>()V

    iput-object p1, p0, Luk/ac/cam/db538/dexter/tests/PropagationTestExerciser;->test:Luk/ac/cam/db538/dexter/tests/PropagationTest;

    iput p2, p0, Luk/ac/cam/db538/dexter/tests/PropagationTestExerciser;->shouldPropagate:Z

    return-void

.end method

.method public constructor <init>(Luk/ac/cam/db538/dexter/tests/PropagationTest;)V
    .registers 3

    # set shouldPropagate = true
    const/4 v0, 1
    invoke-direct {p0, p1, v0}, Luk/ac/cam/db538/dexter/tests/PropagationTestExerciser;-><init>(Luk/ac/cam/db538/dexter/tests/PropagationTest;Z)V

    return-void

.end method

# virtual methods
.method public getTest()Luk/ac/cam/db538/dexter/tests/Test;
    .registers 2

    iget-object v0, p0, Luk/ac/cam/db538/dexter/tests/PropagationTestExerciser;->test:Luk/ac/cam/db538/dexter/tests/PropagationTest;
    return-object v0

.end method

.method private static rand()I
    .registers 2

    sget-object v0, Luk/ac/cam/db538/dexter/tests/PropagationTestExerciser;->RAND:Ljava/util/Random;

    # generate a random positive int

    const v1, 0xFFFF
    invoke-virtual {v0, v1}, Ljava/util/Random;->nextInt(I)I
    move-result v0

    return v0

.end method

.method public run()Z
    .registers 6

    # First run with an untainted argument

    invoke-static {}, Luk/ac/cam/db538/dexter/tests/PropagationTestExerciser;->rand()I
    move-result v0
    iget-object v1, p0, Luk/ac/cam/db538/dexter/tests/PropagationTestExerciser;->test:Luk/ac/cam/db538/dexter/tests/PropagationTest;
    invoke-interface {v1, v0}, Luk/ac/cam/db538/dexter/tests/PropagationTest;->propagate(I)I
    move-result v2

    # ... and check taint of the result

    invoke-static {v2}, Luk/ac/cam/db538/dexter/tests/TaintUtils;->isTainted(I)Z
    move-result v2

    # Now generate a tainted constant

    invoke-static {}, Luk/ac/cam/db538/dexter/tests/PropagationTestExerciser;->rand()I
    move-result v0
    invoke-static {v0}, Luk/ac/cam/db538/dexter/tests/TaintUtils;->taint(I)I
    move-result v0

    # ... run again

    iget-object v1, p0, Luk/ac/cam/db538/dexter/tests/PropagationTestExerciser;->test:Luk/ac/cam/db538/dexter/tests/PropagationTest;
    invoke-interface {v1, v0}, Luk/ac/cam/db538/dexter/tests/PropagationTest;->propagate(I)I
    move-result v3

    # ... and again check the taint of the outcome

    invoke-static {v3}, Luk/ac/cam/db538/dexter/tests/TaintUtils;->isTainted(I)Z
    move-result v3

    # Need: v2 == false && v3 == shouldPropagate

    iget v0, p0, Luk/ac/cam/db538/dexter/tests/PropagationTestExerciser;->shouldPropagate:Z

    if-nez v2, :return_false
    if-ne v3, v0, :return_false

    # All fine, return true

    const/4 v0, 0x1
    return v0

    # Something not fine, return false
    
    :return_false
    const/4 v0, 0x0
    return v0
    
.end method
