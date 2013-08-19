.class public abstract LExceptionTest;
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
.method public abstract execute(Ljava/lang/Object;)V
.end method

.method public abstract expected()Ljava/lang/Class;
.end method

.method public abstract arg()Ljava/lang/Object;
.end method