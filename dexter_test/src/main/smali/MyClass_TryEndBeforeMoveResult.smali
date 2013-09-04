.class public LMyClass_TryEndBeforeMoveResult;
.super Ljava/lang/Object;

.field private static X:I

# direct methods
.method public constructor <init>()V
    .registers 5

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V

    const/4 v0, 0x0 # NULL

    :try_start
    invoke-virtual {p0}, Ljava/lang/Object;->hashCode()I
    :try_end
    .catchall {:try_start .. :try_end} :handler

    move-result v0
    sput v0, LMyClass_TryEndBeforeMoveResult;->X:I

    return-void

    :handler 
    move-exception v1
    invoke-virtual {v0}, Ljava/lang/Object;->hashCode()I

    return-void
    
.end method
