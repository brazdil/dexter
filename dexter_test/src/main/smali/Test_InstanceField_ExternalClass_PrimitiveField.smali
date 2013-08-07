.class public Luk/ac/cam/db538/dexter/tests/Test_InstanceField_ExternalClass_PrimitiveField;
.super Ljava/lang/Object;

# interfaces
.implements Luk/ac/cam/db538/dexter/tests/PropagationTest;

# direct methods
.method public constructor <init>()V
    .registers 2

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V
    return-void
    
.end method

# virtual methods
.method public getName()Ljava/lang/String;
    .registers 2
    
    const-string v0, "IField: ext. class, primitive field"
    return-object v0
    
.end method

.method public getDescription()Ljava/lang/String;
    .registers 2

    const-string v0, "public int field in android.graphics.Point"
    return-object v0
    
.end method

.method public propagate(I)I
    .registers 6

    # create object
    new-instance v2, Landroid/graphics/Point;
    invoke-direct {v2}, Landroid/graphics/Point;-><init>()V

    # propagate
    iput p1, v2, Landroid/graphics/Point;->x:I
    iget v0, v2, Landroid/graphics/Point;->x:I

    return v0
    
.end method
