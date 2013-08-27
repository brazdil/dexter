.class public LTest_ArrayReference_NestedLookup;
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
    
    const-string v0, "Array: reference, nested lookup"
    return-object v0
    
.end method

.method public getDescription()Ljava/lang/String;
    .registers 2

    const-string v0, "tainted Object looked up inside a returned array"
    return-object v0
    
.end method

.method public propagate(I)I
    .registers 9

    # create new Point
    new-instance v0, Landroid/graphics/Point;
    invoke-direct {v0}, Landroid/graphics/Point;-><init>()V

    # v3 = new ArrayList()
    new-instance v3, Ljava/util/ArrayList;
    invoke-direct {v3}, Ljava/util/ArrayList;-><init>()V

    # v3.add(v0)
    invoke-virtual {v3, v0}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    #
    # Now the Point is hidden inside the ArrayList.
    # We taint v0 => ArrayList stays untainted.
    #
    iput p1, v0, Landroid/graphics/Point;->x:I

    # retrieve the objects as an array
    # v4 = v3.toArray()
    invoke-virtual {v3}, Ljava/util/ArrayList;->toArray()[Ljava/lang/Object;
    move-result-object v4

    # v5 = v4[0]
    const/4 v2, 0x0
    aget-object v5, v4, v2
    check-cast v5, Landroid/graphics/Point;

    # return v5.x
    iget v6, v5, Landroid/graphics/Point;->x:I
    return v6

.end method
