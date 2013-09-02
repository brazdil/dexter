.class public LTest_Source_Socket;
.super Ljava/lang/Object;

# interfaces
.implements LSourceTest;

# direct methods
.method public constructor <init>()V
    .registers 2

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V
    return-void

.end method

# virtual methods
.method public getName()Ljava/lang/String;
    .registers 2
    
    const-string v0, "Source: socket"
    return-object v0
    
.end method

.method public getDescription()Ljava/lang/String;
    .registers 2

    const-string v0, "new Socket()"
    return-object v0
    
.end method

.method public generate(Landroid/content/Context;)Ljava/lang/Object;
	.registers 8

    new-instance v0, Ljava/net/Socket;
    invoke-direct {v0}, Ljava/net/Socket;-><init>()V

	return-object v0

.end method
