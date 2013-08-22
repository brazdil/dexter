.class public LTest_CheckCast_Error;
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
    
    const-string v0, "CheckCast: error"
    return-object v0
    
.end method

.method public getDescription()Ljava/lang/String;
    .registers 2

    const-string v0, "x = (Integer) (\"bla bla\")"
    return-object v0
    
.end method

.method public execute(Ljava/lang/Object;)V
    .registers 3

    check-cast p1, Ljava/lang/Integer;
    
    return-void
    
.end method

.method public expected()Ljava/lang/Class;
    .registers 1

    const-class v0, Ljava/lang/ClassCastException;
    return-object v0

.end method

.method public arg()Ljava/lang/Object;
    .registers 1

    const-string v0, "tasty test..."
    return-object v0

.end method