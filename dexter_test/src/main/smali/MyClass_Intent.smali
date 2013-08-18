.class public LMyClass_Intent;
.super Landroid/content/Intent;

# instance fields
.field public val:I

# direct methods
.method public constructor <init>()V
    .registers 4

    invoke-direct {p0}, Landroid/content/Intent;-><init>()V
    return-void
    
.end method

.method public putExtra(Ljava/lang/String;I)Landroid/content/Intent;
	.registers 3

    iput p2, p0, LMyClass_Intent;->val:I
    return-object p0

.end method

.method public getIntExtra(Ljava/lang/String;I)I
	.registers 4

    iget v0, p0, LMyClass_Intent;->val:I
    return v0

.end method

