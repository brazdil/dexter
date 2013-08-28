.class public LMyClass_ObjectField;
.super Ljava/lang/Object;

# instance fields
.field private X:Ljava/lang/Object;

# direct methods
.method public constructor <init>()V
    .registers 2

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V
    return-void
    
.end method

.method public getX()Ljava/lang/Object;
    .registers 1

    iget v0, p0, LMyClass_ObjectField;->X:Ljava/lang/Object;
    return-object v0
    
.end method
