.class public LTest_FillArrayData_Overflow_DataTaint;
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
    
    const-string v0, "FillArrayData: tainting when overflow"
    return-object v0
    
.end method

.method public getDescription()Ljava/lang/String;
    .registers 2

    const-string v0, "no taint overwrite when array too short"
    return-object v0
    
.end method

.method public propagate(I)I
    .registers 4

    # create array
    const/4 v0, 3
    new-array v0, v0, [I

    # store argument in it
    const/4 v1, 0
    aput p1, v0, v1

    # now overwrite with the constant data
#    :try_start
#    fill-array-data v0, :array_data
#    :try_end
#    .catch Ljava/lang/ArrayIndexOutOfBoundsException; {:try_start .. :try_end} :handler

    # should never happen
    return v1

#    :handler
#
#    # retrieve and return
#    aget v0, v0, v1
#    return v0
#
#    :array_data
#    .array-data 0x4
#        0x00t 0x11t 0x22t 0x33t
#        0xfft 0xeet 0xddt 0xcct
#        0xfft 0xeet 0xddt 0xcct
#        0xfft 0xeet 0xddt 0xcct
#    .end array-data
    
.end method
