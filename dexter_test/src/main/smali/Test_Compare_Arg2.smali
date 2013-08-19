.class public LTest_Compare_Arg2;
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
    
    const-string v0, "Compare: arg2, double"
    return-object v0
    
.end method

.method public getDescription()Ljava/lang/String;
    .registers 2

    const-string v0, "cmpl-double rX, rConst, [+]"
    return-object v0
    
.end method

.method public propagate(I)I
    .registers 6

    const-wide v2, 0x0
    int-to-double v4, p1
    cmpl-double v0, v2, v4
    return v0
    
.end method
