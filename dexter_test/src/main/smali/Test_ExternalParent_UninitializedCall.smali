.class public LTest_ExternalParent_UninitializedCall;
.super LNoExceptionTest;

# direct methods
.method public constructor <init>()V
    .registers 1

    invoke-direct {p0}, LNoExceptionTest;-><init>()V
    return-void
    
.end method

# virtual methods
.method public getName()Ljava/lang/String;
    .registers 2
    
    const-string v0, "External parent: uninitialized THIS"
    return-object v0
    
.end method

.method public getDescription()Ljava/lang/String;
    .registers 2

    const-string v0, "extend View, override requestLayout"
    return-object v0
    
.end method

.method public execute(Ljava/lang/Object;)V
    .registers 4

    new-instance v0, LMyClass_View;
    invoke-direct {v0}, LMyClass_View;-><init>()V

    return-void

.end method
