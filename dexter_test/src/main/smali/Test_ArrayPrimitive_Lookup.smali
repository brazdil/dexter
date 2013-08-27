.class public LTest_ArrayPrimitive_Lookup;
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
    
    const-string v0, "Array: primitive, lookup"
    return-object v0
    
.end method

.method public getDescription()Ljava/lang/String;
    .registers 2

    const-string v0, "propagate int array through ArrayList"
    return-object v0
    
.end method

.method public propagate(I)I
    .registers 5

    # v0 = new int[[+]]
    rem-int/lit8 p1, p1, 4
    new-array v0, p1, [I

    # v1 = new ArrayList()
    new-instance v1, Ljava/util/ArrayList;
    invoke-direct {v1}, Ljava/util/ArrayList;-><init>()V

    # v1.add(v0)
    invoke-virtual {v1, v0}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    # v2 = v1.get(0)
    const/4 v0, 0x0
    invoke-virtual {v1, v0}, Ljava/util/ArrayList;->get(I)Ljava/lang/Object;
    move-result-object v2

    # return ((int[]) v2).length
    check-cast v2, [I
    array-length v3, v2
    return v3
    
.end method
