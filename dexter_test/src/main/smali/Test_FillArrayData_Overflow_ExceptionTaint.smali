.class public LTest_FillArrayData_Overflow_ExceptionTaint;
.super LExceptionTest;

# direct methods
.method public constructor <init>()V
    .registers 1

    invoke-direct {p0}, LExceptionTest;-><init>()V
    return-void
    
.end method

# virtual methods
.method public getName()Ljava/lang/String;
    .registers 2
    
    const-string v0, "FillArrayData: tainting overflow exception"
    return-object v0
    
.end method

.method public getDescription()Ljava/lang/String;
    .registers 2

    const-string v0, "overwrite an array that is too short"
    return-object v0
    
.end method

.method public expected()Ljava/lang/Class;
    .registers 1

    const-class v0, Ljava/lang/ArrayIndexOutOfBoundsException;
    return-object v0

.end method

.method public arg()Ljava/lang/Object;
    .registers 1

    # create array that is too short
    const/4 v0, 3
    new-array v0, v0, [I
    return-object v0

.end method

.method public execute(Ljava/lang/Object;)V
    .registers 4

    check-cast p1, [I
    fill-array-data p1, :array_data
    return-void

    :array_data
    .array-data 0x4
        0x00t 0x11t 0x22t 0x33t
        0xfft 0xeet 0xddt 0xcct
        0xfft 0xeet 0xddt 0xcct
        0xfft 0xeet 0xddt 0xcct
    .end array-data
    
.end method
