.class public abstract LNoExceptionTest;
.super LExceptionTest;

# direct methods
.method public constructor <init>()V
    .registers 1

    invoke-direct {p0}, LExceptionTest;-><init>()V
    return-void
    
.end method

# virtual methods
.method public expected()Ljava/lang/Class;
	.registers 1

	# return NULL = no exception
	const/4 v0, 0x0
	return-object v0

.end method

.method public arg()Ljava/lang/Object;
	.registers 1

	# return NULL
	const/4 v0, 0x0
	return-object v0

.end method