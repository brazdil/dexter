.class public LTest_ArrayPrimitive_Index_Put;
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
    
    const-string v0, "Array: primitive, index (put)"
    return-object v0
    
.end method

.method public getDescription()Ljava/lang/String;
    .registers 2

    const-string v0, "APUT with tainted index"
    return-object v0
    
.end method

.method public propagate(I)I
    .registers 5

    # turn argument to zero
    sub-int p1, p1, p1

    # v0 = new int[2]
    const/4 v1, 2
    new-array v0, v1, [I

    # v0[[+]] = 1
    const/4 v1, 1
    aput v1, v0, p1

    # return v0[0]
    const/4 v1, 0
    aget v0, v0, v1
    return v0
    
.end method
