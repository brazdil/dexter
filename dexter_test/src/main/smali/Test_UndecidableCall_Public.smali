.class public Luk/ac/cam/db538/dexter/tests/Test_UndecidableCall_Public;
.super Ljava/lang/Object;

# interfaces
.implements Luk/ac/cam/db538/dexter/tests/PropagationTest;


# direct methods
.method public constructor <init>()V
    .registers 1

    invoke-direct {p0}, Ljava/lang/Object;-><init>()V
    return-void
    
.end method


# virtual methods
.method public getName()Ljava/lang/String;
    .registers 2
    
    const-string v0, "Undecidable call: public method"
    return-object v0
    
.end method

.method public getDescription()Ljava/lang/String;
    .registers 2

    const-string v0, "two implementations of Intent.get/putExtra"
    return-object v0
    
.end method

.method public propagate(I)I
    .registers 16

    # create external object
    new-instance v2, Landroid/content/Intent;
    invoke-direct {v2}, Landroid/content/Intent;-><init>()V

    # create internal object
    new-instance v3, Luk/ac/cam/db538/dexter/tests/MyClass_Intent;
    invoke-direct {v3}, Luk/ac/cam/db538/dexter/tests/MyClass_Intent;-><init>()V

    # "randomly" swap them
    const/4 v0, 7
    if-lt v0, p1, :end
    move-object v4, v2
    move-object v2, v3
    move-object v3, v4
    :end

    # we'll need some meaningless constants
    const/4 v0, 0 
    const-string v1, "TEST"

    # propagate through the undecidable objects
    invoke-virtual {v2, v1, p1}, Landroid/content/Intent;->putExtra(Ljava/lang/String;I)Landroid/content/Intent;
    invoke-virtual {v2, v1, v0}, Landroid/content/Intent;->getIntExtra(Ljava/lang/String;I)I
    move-result v4
    invoke-virtual {v3, v1, v4}, Landroid/content/Intent;->putExtra(Ljava/lang/String;I)Landroid/content/Intent;
    invoke-virtual {v3, v1, v0}, Landroid/content/Intent;->getIntExtra(Ljava/lang/String;I)I
    move-result v5

    return v5
.end method
