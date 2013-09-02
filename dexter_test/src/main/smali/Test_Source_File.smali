.class public LTest_Source_File;
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
    
    const-string v0, "Source: file"
    return-object v0
    
.end method

.method public getDescription()Ljava/lang/String;
    .registers 2

    const-string v0, "File.createTempFile(\"xyz\", \"\")"
    return-object v0
    
.end method

.method public generate(Landroid/content/Context;)Ljava/lang/Object;
	.registers 8

    const-string v0, "xyz"
    const-string v1, ""
    invoke-static {v0, v1}, Ljava/io/File;->createTempFile(Ljava/lang/String;Ljava/lang/String;)Ljava/io/File;
    move-result-object v2

	return-object v2

.end method
