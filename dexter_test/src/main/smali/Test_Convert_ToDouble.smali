.class public LTest_Convert_ToDouble;
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
    
    const-string v0, "Convert: to double"
    return-object v0
    
.end method

.method public getDescription()Ljava/lang/String;
    .registers 2

    const-string v0, "int-to-double rX, [+]\ndouble-to-int rY, rX"
    return-object v0
    
.end method

.method public propagate(I)I
    .registers 4

    int-to-double v1, p1
    double-to-int v0, v1
    return v0
    
.end method
