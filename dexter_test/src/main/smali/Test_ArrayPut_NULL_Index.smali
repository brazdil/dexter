.class public LTest_ArrayPut_NULL_Index;
.super LNullExceptionTest;

# direct methods
.method public constructor <init>()V
    .registers 1

    invoke-direct {p0}, LNullExceptionTest;-><init>()V
    return-void
    
.end method

# virtual methods
.method public getName()Ljava/lang/String;
    .registers 2
    
    const-string v0, "ArrayPut: NULL array, tainted index"
    return-object v0
    
.end method

.method public getDescription()Ljava/lang/String;
    .registers 2

    const-string v0, "aput rX, NULL, [+]"
    return-object v0
    
.end method

.method public execute(Ljava/lang/Object;)V
    .registers 3

    # turn ref into int
    invoke-static {p1}, LPropagationTest;->ref2int(Ljava/lang/Object;)I
    move-result v0
    
    const/4 v1, 0x0
    aput-object p1, v1, v0

    return-void
    
.end method
