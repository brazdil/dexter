.class public LTest_ArrayPut_NULL;
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
    
    const-string v0, "ArrayPut: tainted NULL array"
    return-object v0
    
.end method

.method public getDescription()Ljava/lang/String;
    .registers 2

    const-string v0, "aput rX, NULL[+], rY"
    return-object v0
    
.end method

.method public execute(Ljava/lang/Object;)V
    .registers 3

#    check-cast p1, [Ljava/lang/Object;
#
#    const/4 v0, 0x0
#    const/4 v1, 0x0
#    aput-object v0, p1, v1

    return-void
    
.end method
