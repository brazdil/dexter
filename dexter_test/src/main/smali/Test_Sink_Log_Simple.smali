.class public LTest_Sink_Log_Simple;
.super Ljava/lang/Object;

# interfaces
.implements LSinkTest;

# direct methods
.method public constructor <init>()V
    .registers 2

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V
    return-void

.end method

# virtual methods
.method public getName()Ljava/lang/String;
    .registers 2
    
    const-string v0, "Sink: log (simple)"
    return-object v0
    
.end method

.method public getDescription()Ljava/lang/String;
    .registers 2

    const-string v0, "Log.v(\"xyz\", [+])"
    return-object v0
    
.end method

.method public leak(Ljava/lang/Object;Landroid/content/Context;)V
	.registers 8

    check-cast p1, Ljava/lang/StringBuilder;

    const-string v0, "DEXTER_TEST_SINK"
    invoke-virtual {p1}, Ljava/lang/StringBuilder;->toString()Ljava/lang/String;
    move-result-object v1

    invoke-static {v0, v1}, Landroid/util/Log;->i(Ljava/lang/String;Ljava/lang/String;)I

    return-void

.end method

.method public arg()Ljava/lang/Object;
    .registers 2

    const-string v0, "Supersecret text..."
    new-instance v1, Ljava/lang/StringBuilder;
    invoke-direct {v1, v0}, Ljava/lang/StringBuilder;-><init>(Ljava/lang/String;)V

    return-object v1

.end method