.class public LTest_ArrayPrimitive_Element;
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
    
    const-string v0, "Array: primitive, element"
    return-object v0
    
.end method

.method public getDescription()Ljava/lang/String;
    .registers 2

    const-string v0, "propagate through int[0]"
    return-object v0
    
.end method

.method public propagate(I)I
    .registers 5

    # v0 = new int[2]
    const/4 v0, 2
    new-array v0, v0, [I
    
    # v0[0] = [+]
    const/4 v1, 0
    aput p1, v0, v1

    # v0[1] = v0[0] ; the point is to test overwriting index register
    aget v1, v0, v1
    const/4 v2, 1
    aput v1, v0, v2

    # return v0[0]
    aget v0, v0, v2
    return v0
    
.end method
