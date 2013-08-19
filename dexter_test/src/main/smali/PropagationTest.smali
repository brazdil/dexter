.class public abstract LPropagationTest;
.super Ljava/lang/Object;

# interfaces
.implements LTest;

# direct methods
.method public constructor <init>()V
    .registers 1

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V
    return-void
    
.end method

# virtual methods
.method public abstract propagate(I)I
.end method

.method public expected()Z
	.registers 1

	# return true
	const/4 v0, 1
	return v0

.end method
