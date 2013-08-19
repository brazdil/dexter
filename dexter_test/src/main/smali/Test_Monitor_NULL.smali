.class public LTest_Monitor_NULL;
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
    
    const-string v0, "Monitor: NULL"
    return-object v0
    
.end method

.method public getDescription()Ljava/lang/String;
    .registers 2

    const-string v0, "monitor-enter [+]"
    return-object v0
    
.end method

.method public execute(Ljava/lang/Object;)V
    .registers 3

    monitor-enter p1
    monitor-exit p1
    
    return-void
    
.end method
