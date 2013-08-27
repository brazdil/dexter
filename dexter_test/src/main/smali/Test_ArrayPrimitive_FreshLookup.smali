.class public LTest_ArrayPrimitive_FreshLookup;
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
    
    const-string v0, "Array: primitive, fresh lookup"
    return-object v0
    
.end method

.method public getDescription()Ljava/lang/String;
    .registers 2

    const-string v0, "external call returning an int array"
    return-object v0
    
.end method

.method public propagate(I)I
    .registers 5

    # v0 = new int[3]
    const/4 v1, 3
    new-array v0, v1, [I

    # v0[1] = [+]
    const/4 v1, 1
    aput p1, v0, v1

    # v2 = Arrays.copyOf(v0, 2)
    const/4 v1, 2
    invoke-static {v0, v1}, Ljava/util/Arrays;->copyOf([II)[I
    move-result-object v2

    # return v2.length (originally not tainted, so must have created a fresh one)
    array-length v3, v2
    return v3
    
.end method
