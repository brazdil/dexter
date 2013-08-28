.class public LTest_ExternalCall_Exception;
.super LExceptionTest;

# direct methods
.method public constructor <init>()V
    .registers 1

    invoke-direct {p0}, LExceptionTest;-><init>()V
    return-void
    
.end method

# virtual methods
.method public getName()Ljava/lang/String;
    .registers 2
    
    const-string v0, "External call: exception"
    return-object v0
    
.end method

.method public getDescription()Ljava/lang/String;
    .registers 2

    const-string v0, "forced StringBuilder exception"
    return-object v0
    
.end method

.method public execute(Ljava/lang/Object;)V
    .registers 7

    check-cast p1, Ljava/lang/String;

    # v1 = new StringBuilder()
    new-instance v1, Ljava/lang/StringBuilder;
    invoke-direct {v1}, Ljava/lang/StringBuilder;-><init>()V

    # v1.replace(-1, -1, [+])
    const/4 v0, -1
    invoke-virtual {v1, v0, v0, p1}, Ljava/lang/StringBuilder;->replace(IILjava/lang/String;)Ljava/lang/StringBuilder;
    return-void

.end method

.method public expected()Ljava/lang/Class;
    .registers 1

    const-class v0, Ljava/lang/StringIndexOutOfBoundsException;
    return-object v0

.end method

.method public arg()Ljava/lang/Object;
    .registers 1

    const-string v0, "xyz"
    return-object v0

.end method