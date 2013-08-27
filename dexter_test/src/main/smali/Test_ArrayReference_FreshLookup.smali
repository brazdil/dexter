.class public LTest_ArrayReference_FreshLookup;
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
    
    const-string v0, "Array: reference, fresh lookup"
    return-object v0
    
.end method

.method public getDescription()Ljava/lang/String;
    .registers 2

    const-string v0, "external call returning an Object array"
    return-object v0
    
.end method

.method public propagate(I)I
    .registers 5

    # v0 = new Integer[3]
    const/4 v1, 3
    new-array v0, v1, [Ljava/lang/Integer;

    # v0[1] = [+]
    const/4 v1, 1
    invoke-static {p1}, Ljava/lang/Integer;->valueOf(I)Ljava/lang/Integer;
    move-result-object p1
    aput-object p1, v0, v1

    # v2 = Arrays.copyOf(v0, 2)
    const/4 v1, 2
    invoke-static {v0, v1}, Ljava/util/Arrays;->copyOf([Ljava/lang/Object;I)[Ljava/lang/Object;
    move-result-object v2
    check-cast v2, [Ljava/lang/Integer;

    # return v2.length (originally not tainted, so must have created a fresh one)
    array-length v3, v2
    return v3
    
.end method
