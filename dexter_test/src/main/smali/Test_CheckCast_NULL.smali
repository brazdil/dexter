.class public LTest_CheckCast_NULL;
.super LPropagationTest;

# direct methods
.method public constructor <init>()V
    .registers 1

    invoke-direct {p0}, LPropagationTest;-><init>()V
    return-void
    
.end method

# virtual methods
.method public getName()Ljava/lang/String;
    .registers 2
    
    const-string v0, "CheckCast: NULL reference"
    return-object v0
    
.end method

.method public getDescription()Ljava/lang/String;
    .registers 2

    const-string v0, "cast to all possible types and check it propagates"
    return-object v0
    
.end method

.method public propagate(I)I
    .registers 5

    # convert argument to NULL
    invoke-virtual {p0, p1}, LPropagationTest;->int2null(I)Ljava/lang/Object;
    move-result v0


    # cast to internal
    check-cast v0, LMyClass_IntField;

    # cast to primitive array
    check-cast v0, [I

    # cast to reference array
    check-cast v0, [Ljava/lang/Class;

    # cast to undecidable
    check-cast v0, Landroid/graphics/Point;

    # cast to external
    check-cast v0, Ljava/lang/String;


    # convert back to an int
    invoke-virtual {p0, v0}, LPropagationTest;->ref2int(Ljava/lang/Object;)I
    move-result v1

    return v1
    
.end method
