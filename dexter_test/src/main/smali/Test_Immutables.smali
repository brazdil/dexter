.class public LTest_Immutables;
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
    
    const-string v0, "Immutables"
    return-object v0
    
.end method

.method public getDescription()Ljava/lang/String;
    .registers 2

    const-string v0, "clean String passed to tainted external call"
    return-object v0
    
.end method

.method public propagate(I)I
    .registers 4

    # create untainted String
    new-instance v0, Ljava/lang/String;
    invoke-direct {v0}, Ljava/lang/String;-><init>()V

    # create tainted ArrayList
    rem-int/lit8 p1, p1, 4
    new-instance v1, Ljava/util/ArrayList;
    invoke-direct {v1, p1}, Ljava/util/ArrayList;-><init>(I)V

    # use the immutable in external call
    # should NOT get tainted
    invoke-virtual {v1, v0}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    # convert immutable to an int
    invoke-static {v0}, LPropagationTest;->ref2int(Ljava/lang/Object;)I
    move-result v0

    return v0
    
.end method

.method public expected()Z
    .registers 1

    # return false
    const/4 v0, 0
    return v0

.end method
