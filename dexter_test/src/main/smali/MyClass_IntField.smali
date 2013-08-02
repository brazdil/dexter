.class public Luk/ac/cam/db538/dexter/tests/MyClass_IntField;
.super Ljava/lang/Object;

# instance fields
.field private X:I

# direct methods
.method public constructor <init>(I)V
    .registers 2

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V
    iput p1, p0, Luk/ac/cam/db538/dexter/tests/MyClass_IntField;->X:I

    return-void
    
.end method

.method public getX()I
    .registers 1

    iget v0, p0, Luk/ac/cam/db538/dexter/tests/MyClass_IntField;->X:I
    return v0
    
.end method
