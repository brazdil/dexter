.class public LTest_Throw_NULL;
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
    
    const-string v0, "Throw: NULL argument"
    return-object v0
    
.end method

.method public getDescription()Ljava/lang/String;
    .registers 2

    const-string v0, "throw NULL[+]"
    return-object v0
    
.end method

.method public execute(Ljava/lang/Object;)V
    .registers 7

    check-cast p1, Ljava/lang/Exception;
    throw p1

.end method
