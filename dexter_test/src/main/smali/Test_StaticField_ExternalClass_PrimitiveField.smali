.class public LTest_StaticField_ExternalClass_PrimitiveField;
.super Ljava/lang/Object;

# interfaces
.implements LPropagationTest;

# direct methods
.method public constructor <init>()V
    .registers 2

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V
    return-void
    
.end method

# virtual methods
.method public getName()Ljava/lang/String;
    .registers 2
    
    const-string v0, "SField: ext. class, primitive field"
    return-object v0
    
.end method

.method public getDescription()Ljava/lang/String;
    .registers 2

    const-string v0, "public int field in android.view.animation.Transformation"
    return-object v0
    
.end method

.method public propagate(I)I
    .registers 3

    # propagate
    sput p1, Landroid/view/animation/Transformation;->TYPE_ALPHA:I
    sget v0, Landroid/view/animation/Transformation;->TYPE_ALPHA:I

    return v0
    
.end method
