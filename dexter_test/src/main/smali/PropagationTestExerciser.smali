.class public LPropagationTestExerciser;
.super LTestExerciser;
.source "PropagationTestExerciser.java"


.field private final test:LPropagationTest;
.field private final shouldPropagate:Z

.field private static final RAND:Ljava/util/Random;

.method static constructor <clinit>()V
    .registers 1

    new-instance v0, Ljava/util/Random;
    invoke-direct {v0}, Ljava/util/Random;-><init>()V
    sput-object v0, LPropagationTestExerciser;->RAND:Ljava/util/Random;

    return-void

.end method

# direct methods
.method public constructor <init>(LPropagationTest;Z)V
    .registers 4

    invoke-direct {p0}, LTestExerciser;-><init>()V

    iput-object p1, p0, LPropagationTestExerciser;->test:LPropagationTest;

    iput p2, p0, LPropagationTestExerciser;->shouldPropagate:Z

    return-void

.end method

.method public constructor <init>(LPropagationTest;)V
    .registers 3

    # set shouldPropagate = true
    const/4 v0, 1
    invoke-direct {p0, p1, v0}, LPropagationTestExerciser;-><init>(LPropagationTest;Z)V

    return-void

.end method

# virtual methods
.method public getTest()LTest;
    .registers 2

    iget-object v0, p0, LPropagationTestExerciser;->test:LPropagationTest;
    return-object v0

.end method

.method private static rand()I
    .registers 2

    sget-object v0, LPropagationTestExerciser;->RAND:Ljava/util/Random;

    # generate a random positive int

    const v1, 0xFFFF
    invoke-virtual {v0, v1}, Ljava/util/Random;->nextInt(I)I
    move-result v0

    return v0

.end method

.method public run()Z
    .registers 6

    # First run with an untainted argument

    invoke-static {}, LPropagationTestExerciser;->rand()I
    move-result v0
    iget-object v1, p0, LPropagationTestExerciser;->test:LPropagationTest;
    invoke-interface {v1, v0}, LPropagationTest;->propagate(I)I
    move-result v2

    # ... and check taint of the result

    invoke-static {v2}, LTaintUtils;->isTainted(I)Z
    move-result v2

    # Now generate a tainted constant

    invoke-static {}, LPropagationTestExerciser;->rand()I
    move-result v0
    invoke-static {v0}, LTaintUtils;->taint(I)I
    move-result v0

    # ... run again

    iget-object v1, p0, LPropagationTestExerciser;->test:LPropagationTest;
    invoke-interface {v1, v0}, LPropagationTest;->propagate(I)I
    move-result v3

    # ... and again check the taint of the outcome

    invoke-static {v3}, LTaintUtils;->isTainted(I)Z
    move-result v3

    # Need: v2 == false && v3 == shouldPropagate

    iget v0, p0, LPropagationTestExerciser;->shouldPropagate:Z

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
