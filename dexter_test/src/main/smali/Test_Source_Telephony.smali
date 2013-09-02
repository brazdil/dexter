.class public LTest_Source_Telephony;
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
    
    const-string v0, "Source: telephony"
    return-object v0
    
.end method

.method public getDescription()Ljava/lang/String;
    .registers 2

    const-string v0, "context.getSystemService(Context.TELEPHONY_SERVICE)"
    return-object v0
    
.end method

.method public generate(Landroid/content/Context;)Ljava/lang/Object;
	.registers 8

	# v1 = Context.TELEPHONY_SERVICE
	sget v1, Landroid/content/Context;->TELEPHONY_SERVICE:Ljava/lang/String;

	# v0 = p1.getContentResolver()
	invoke-virtual {p1, v1}, Landroid/content/Context;->getSystemService(Ljava/lang/String;)Ljava/lang/Object;
	move-result-object v0

	return-object v0

.end method
