.class public LTest_FillArrayData_NULL;
.super LNullExceptionTest;

# direct methods
.method public constructor <init>()V
    .registers 1

    invoke-direct {p0}, LNullExceptionTest;-><init>()V
    return-void
    
.end method

# virtual methods
.method public getName()Ljava/lang/String;
    .registers 2
    
    const-string v0, "FillArrayData: NULL array"
    return-object v0
    
.end method

.method public getDescription()Ljava/lang/String;
    .registers 2

    const-string v0, "fill-array-data NULL[+], :data"
    return-object v0
    
.end method

.method public execute(Ljava/lang/Object;)V
    .registers 3

    check-cast p1, [I
    fill-array-data p1, :array_data
    return-void

#    const-string v5, "DexterTest"
#    const-string v0, "Fuck you"
#    invoke-static {v5, v0}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

#    const/4 v0, 0
#    const/4 v1, 1
#    new-array v2, v1, [I
#    const v1, 1234
#    aput v1, v2, v0

#    :try_start
#    fill-array-data v2, :array_data
#    :try_end
#    .catchall {:try_start .. :try_end} :handler

#    goto :after

#    :handler 
#    move-exception v1
#    invoke-virtual {v1}, Ljava/lang/Throwable;->printStackTrace()V    

#    aget v0, v2, v0
#    invoke-static {v0}, Ljava/lang/Integer;->toString(I)Ljava/lang/String;
#    move-result v0
#    invoke-static {v5, v0}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I
#    :after
#   return-void

    :array_data
    .array-data 0x4
        0x00t 0x11t 0x22t 0x33t
        0xfft 0xeet 0xddt 0xcct
    .end array-data

.end method
