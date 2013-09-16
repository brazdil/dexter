.class public LMyClass_IntField;
.super Ljava/lang/Object;

# instance fields
.field private X:I

# direct methods
.method public constructor <init>(I)V
    .registers 2

    # Doing this BEFORE the initializer as a regression test
    # against initializing taint filds AFTER the superclass
    # constructor is called

    iput p1, p0, LMyClass_IntField;->X:I

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V
    return-void
    
.end method

.method public getX()I
    .registers 1

    iget v0, p0, LMyClass_IntField;->X:I
    return v0
    
.end method
