.class public LTest_Source_Browser;
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
    
    const-string v0, "Source: browser"
    return-object v0
    
.end method

.method public getDescription()Ljava/lang/String;
    .registers 2

    const-string v0, "Browser.getAllVisitedUrls(cr)"
    return-object v0
    
.end method

.method public generate(Landroid/content/Context;)Ljava/lang/Object;
	.registers 8

    # v0 = p1.getContentResolver()
    invoke-virtual {p1}, Landroid/content/Context;->getContentResolver()Landroid/content/ContentResolver;
    move-result-object v0

    # v1 = Browser.getAllVisitedUrls(v0)
    invoke-static {v0}, Landroid/provider/Browser;->getAllVisitedUrls(Landroid/content/ContentResolver;)Landroid/database/Cursor;
    move-result-object v1

	return-object v1

.end method
