.class public LTest_External;
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
    
    const-string v0, "External"
    return-object v0
    
.end method

.method public getDescription()Ljava/lang/String;
    .registers 2

    const-string v0, "rX = new ArrayList([+]); return rX.size();"
    return-object v0
    
.end method

.method public propagate(I)I
    .registers 3

    # size = param % 4
    rem-int/lit8 p1, p1, 0x4

    new-instance v1, Ljava/util/ArrayList;
    invoke-direct {v1, p1}, Ljava/util/ArrayList;-><init>(I)V

    invoke-virtual {v1}, Ljava/util/ArrayList;->size()I
    move-result v0
    return v0
    
.end method
