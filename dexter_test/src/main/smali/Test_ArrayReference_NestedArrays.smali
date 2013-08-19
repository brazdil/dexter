.class public LTest_ArrayReference_NestedArrays;
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
    
    const-string v0, "Array: nested arrays"
    return-object v0
    
.end method

.method public getDescription()Ljava/lang/String;
    .registers 2

    const-string v0, "force Taint object cast after AGET"
    return-object v0
    
.end method

.method public propagate(I)I
    .registers 6

    # create arrays
    const/4 v3, 1
    new-array v2, v3, [I
    new-array v1, v3, [[I
    new-array v0, v3, [[[I
    
    # nest arrays inside each other
    const/4 v3, 0
    aput-object v2, v1, v3
    aput-object v1, v0, v3

    # store the arg inside v2
    aput p1, v2, v3

    # now retrieve it from the nested references
    aget-object v0, v0, v3
    aget-object v0, v0, v3   # will fail unless the previous insn casts the Taint object to TaintArrayReference
    aget v0, v0, v3          # will fail unless the previous insn casts the Taint object to TaintArrayPrimitive

    return v0
    
.end method
