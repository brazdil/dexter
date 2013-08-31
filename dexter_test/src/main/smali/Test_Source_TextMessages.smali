.class public LTest_Source_TextMessages;
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
    
    const-string v0, "Source: text messages"
    return-object v0
    
.end method

.method public getDescription()Ljava/lang/String;
    .registers 2

    const-string v0, "cr.query(\"content://sms/inbox/\", ...)"
    return-object v0
    
.end method

.method public generate(Landroid/content/Context;)Ljava/lang/Object;
	.registers 8

	# v0 = p1.getContentResolver()
	invoke-virtual {p1}, Landroid/content/Context;->getContentResolver()Landroid/content/ContentResolver;
	move-result-object v0

	# v1 = Uri.parse("content://sms/inbox/")
	const-string v1, "content://sms/inbox/"
	invoke-static {v1}, Landroid/net/Uri;->parse(Ljava/lang/String;)Landroid/net/Uri;
	move-result-object v1

	# v2 .. v5 = NULL
	const/4 v2, 0x0
	const/4 v3, 0x0
	const/4 v4, 0x0
	const/4 v5, 0x0

	# v6 = v0.query(v1, NULL, NULL, NULL, NULL)
	invoke-virtual/range {v0 .. v5}, Landroid/content/ContentResolver;->query(Landroid/net/Uri;[Ljava/lang/String;Ljava/lang/String;[Ljava/lang/String;Ljava/lang/String;)Landroid/database/Cursor;
	move-result v6
	return-object v6

.end method
