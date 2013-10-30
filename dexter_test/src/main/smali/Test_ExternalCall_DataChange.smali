.class public LTest_ExternalCall_DataChange;
.super LPropagationTest;

# direct methods
.method public constructor <init>()V
    .registers 1

    invoke-direct {p0}, LPropagationTest;-><init>()V
    return-void
    
.end method


# virtual methods
.method public getName()Ljava/lang/String;
    .registers 2
    
    const-string v0, "External call: data change"
    return-object v0
    
.end method

.method public getDescription()Ljava/lang/String;
    .registers 2

    const-string v0, "rList.toArray(rArray)"
    return-object v0
    
.end method

.method public propagate(I)I
    .registers 5

    # create an ArrayList
    new-instance v0, Ljava/util/ArrayList;
    invoke-direct {v0}, Ljava/util/ArrayList;-><init>()V

    # create an internal class instance, containing tainted value
    new-instance v1, LMyClass_Point;
    invoke-direct {v1}, LMyClass_Point;-><init>()V
    invoke-virtual {v1, p1}, LMyClass_Point;->setX(I)V

    # add it into the list, and forget reference
    invoke-virtual {v0, v1}, Ljava/util/ArrayList;->add(Ljava/lang/Object;)Z

    # create an array
    const/4 v1, 1
    new-array v1, v1, [Landroid/graphics/Point;

    # call list.toArray(array)
    invoke-virtual {v0, v1}, Ljava/util/ArrayList;->toArray([Ljava/lang/Object;)[Ljava/lang/Object;

    # retrieve the object from the array
    const/4 v0, 0x0
    aget-object v0, v1, v0

    # call a method with the object
    # need internal method...
    invoke-virtual {v0, v1}, Ljava/lang/Object;->equals(Ljava/lang/Object;)Z

    # retrieve the value from it and return
    iget v0, v0, Landroid/graphics/Point;->x:I

    return v0
    
.end method
