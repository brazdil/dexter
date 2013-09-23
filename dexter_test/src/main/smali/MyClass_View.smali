.class public LMyClass_View;
.super Landroid/view/ViewGroup;

# direct methods
.method public constructor <init>()V
    .registers 2

    invoke-static {}, LTestList;->getContext()Landroid/content/Context;
    move-result v0

    invoke-direct {p0, v0}, Landroid/view/ViewGroup;-><init>(Landroid/content/Context;)V
    return-void
    
.end method

# virtual methods
.method public requestLayout()V
	.registers 2

	return-void

.end method