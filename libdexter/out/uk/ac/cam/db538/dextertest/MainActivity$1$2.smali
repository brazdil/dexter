.class Luk/ac/cam/db538/dextertest/MainActivity$1$2;
.super Ljava/lang/Thread;
.source "MainActivity.java"


# annotations
.annotation system Ldalvik/annotation/EnclosingMethod;
    value = Luk/ac/cam/db538/dextertest/MainActivity$1;->onClick(Landroid/view/View;)V
.end annotation

.annotation system Ldalvik/annotation/InnerClass;
    accessFlags = 0x0
    name = null
.end annotation


# instance fields
.field final synthetic this$1:Luk/ac/cam/db538/dextertest/MainActivity$1;

.field private final synthetic val$context:Landroid/content/Context;

.field private final synthetic val$threadHandler:Landroid/os/Handler;


# direct methods
.method constructor <init>(Luk/ac/cam/db538/dextertest/MainActivity$1;Landroid/content/Context;Landroid/os/Handler;)V
    .registers 4
    .parameter
    .parameter
    .parameter

    .prologue
    .line 1
    iput-object p1, p0, Luk/ac/cam/db538/dextertest/MainActivity$1$2;->this$1:Luk/ac/cam/db538/dextertest/MainActivity$1;

    iput-object p2, p0, Luk/ac/cam/db538/dextertest/MainActivity$1$2;->val$context:Landroid/content/Context;

    iput-object p3, p0, Luk/ac/cam/db538/dextertest/MainActivity$1$2;->val$threadHandler:Landroid/os/Handler;

    .line 77
    invoke-direct {p0}, Ljava/lang/Thread;-><init>()V

    return-void
.end method


# virtual methods
.method public run()V
    .registers 7

    .prologue
    .line 80
    const/4 v3, 0x0

    .line 81
    .local v3, source:Luk/ac/cam/db538/dextertest/Source;
    const/4 v2, 0x0

    .line 83
    .local v2, sink:Luk/ac/cam/db538/dextertest/Sink;
    sget-object v4, Ljava/lang/System;->out:Ljava/io/PrintStream;

    const-string v5, "picking source"

    invoke-virtual {v4, v5}, Ljava/io/PrintStream;->println(Ljava/lang/String;)V

    .line 85
    iget-object v4, p0, Luk/ac/cam/db538/dextertest/MainActivity$1$2;->this$1:Luk/ac/cam/db538/dextertest/MainActivity$1;

    #getter for: Luk/ac/cam/db538/dextertest/MainActivity$1;->this$0:Luk/ac/cam/db538/dextertest/MainActivity;
    invoke-static {v4}, Luk/ac/cam/db538/dextertest/MainActivity$1;->access$0(Luk/ac/cam/db538/dextertest/MainActivity$1;)Luk/ac/cam/db538/dextertest/MainActivity;

    move-result-object v4

    #getter for: Luk/ac/cam/db538/dextertest/MainActivity;->spinnerSource:Landroid/widget/Spinner;
    invoke-static {v4}, Luk/ac/cam/db538/dextertest/MainActivity;->access$0(Luk/ac/cam/db538/dextertest/MainActivity;)Landroid/widget/Spinner;

    move-result-object v4

    invoke-virtual {v4}, Landroid/widget/Spinner;->getSelectedItemId()J

    move-result-wide v4

    long-to-int v4, v4

    packed-switch v4, :pswitch_data_a8

    .line 106
    :goto_1b
    sget-object v4, Ljava/lang/System;->out:Ljava/io/PrintStream;

    const-string v5, "picking sink"

    invoke-virtual {v4, v5}, Ljava/io/PrintStream;->println(Ljava/lang/String;)V

    .line 108
    iget-object v4, p0, Luk/ac/cam/db538/dextertest/MainActivity$1$2;->this$1:Luk/ac/cam/db538/dextertest/MainActivity$1;

    #getter for: Luk/ac/cam/db538/dextertest/MainActivity$1;->this$0:Luk/ac/cam/db538/dextertest/MainActivity;
    invoke-static {v4}, Luk/ac/cam/db538/dextertest/MainActivity$1;->access$0(Luk/ac/cam/db538/dextertest/MainActivity$1;)Luk/ac/cam/db538/dextertest/MainActivity;

    move-result-object v4

    #getter for: Luk/ac/cam/db538/dextertest/MainActivity;->spinnerSink:Landroid/widget/Spinner;
    invoke-static {v4}, Luk/ac/cam/db538/dextertest/MainActivity;->access$1(Luk/ac/cam/db538/dextertest/MainActivity;)Landroid/widget/Spinner;

    move-result-object v4

    invoke-virtual {v4}, Landroid/widget/Spinner;->getSelectedItemId()J

    move-result-wide v4

    long-to-int v4, v4

    packed-switch v4, :pswitch_data_b8

    .line 127
    :goto_34
    :try_start_34
    sget-object v4, Ljava/lang/System;->out:Ljava/io/PrintStream;

    const-string v5, "getting data"

    invoke-virtual {v4, v5}, Ljava/io/PrintStream;->println(Ljava/lang/String;)V

    .line 128
    iget-object v4, p0, Luk/ac/cam/db538/dextertest/MainActivity$1$2;->val$context:Landroid/content/Context;

    invoke-interface {v3, v4}, Luk/ac/cam/db538/dextertest/Source;->getData(Landroid/content/Context;)Ljava/lang/String;

    move-result-object v0

    .line 129
    .local v0, data:Ljava/lang/String;
    sget-object v4, Ljava/lang/System;->out:Ljava/io/PrintStream;

    const-string v5, "sending data"

    invoke-virtual {v4, v5}, Ljava/io/PrintStream;->println(Ljava/lang/String;)V

    .line 130
    iget-object v4, p0, Luk/ac/cam/db538/dextertest/MainActivity$1$2;->val$context:Landroid/content/Context;

    invoke-interface {v2, v0, v4}, Luk/ac/cam/db538/dextertest/Sink;->sendData(Ljava/lang/String;Landroid/content/Context;)V

    .line 131
    sget-object v4, Ljava/lang/System;->out:Ljava/io/PrintStream;

    const-string v5, "finishing"

    invoke-virtual {v4, v5}, Ljava/io/PrintStream;->println(Ljava/lang/String;)V

    .line 132
    iget-object v4, p0, Luk/ac/cam/db538/dextertest/MainActivity$1$2;->val$threadHandler:Landroid/os/Handler;

    const/4 v5, 0x0

    invoke-virtual {v4, v5}, Landroid/os/Handler;->sendEmptyMessage(I)Z
    :try_end_5a
    .catch Ljava/lang/RuntimeException; {:try_start_34 .. :try_end_5a} :catch_9d

    .line 137
    .end local v0           #data:Ljava/lang/String;
    :goto_5a
    return-void

    .line 87
    :pswitch_5b
    new-instance v3, Luk/ac/cam/db538/dextertest/Source_Contacts;

    .end local v3           #source:Luk/ac/cam/db538/dextertest/Source;
    invoke-direct {v3}, Luk/ac/cam/db538/dextertest/Source_Contacts;-><init>()V

    .line 88
    .restart local v3       #source:Luk/ac/cam/db538/dextertest/Source;
    goto :goto_1b

    .line 90
    :pswitch_61
    new-instance v3, Luk/ac/cam/db538/dextertest/Source_TextMessages;

    .end local v3           #source:Luk/ac/cam/db538/dextertest/Source;
    invoke-direct {v3}, Luk/ac/cam/db538/dextertest/Source_TextMessages;-><init>()V

    .line 91
    .restart local v3       #source:Luk/ac/cam/db538/dextertest/Source;
    goto :goto_1b

    .line 93
    :pswitch_67
    new-instance v3, Luk/ac/cam/db538/dextertest/Source_CallLog;

    .end local v3           #source:Luk/ac/cam/db538/dextertest/Source;
    invoke-direct {v3}, Luk/ac/cam/db538/dextertest/Source_CallLog;-><init>()V

    .line 94
    .restart local v3       #source:Luk/ac/cam/db538/dextertest/Source;
    goto :goto_1b

    .line 96
    :pswitch_6d
    new-instance v3, Luk/ac/cam/db538/dextertest/Source_Browser;

    .end local v3           #source:Luk/ac/cam/db538/dextertest/Source;
    invoke-direct {v3}, Luk/ac/cam/db538/dextertest/Source_Browser;-><init>()V

    .line 97
    .restart local v3       #source:Luk/ac/cam/db538/dextertest/Source;
    goto :goto_1b

    .line 99
    :pswitch_73
    new-instance v3, Luk/ac/cam/db538/dextertest/Source_Location;

    .end local v3           #source:Luk/ac/cam/db538/dextertest/Source;
    invoke-direct {v3}, Luk/ac/cam/db538/dextertest/Source_Location;-><init>()V

    .line 100
    .restart local v3       #source:Luk/ac/cam/db538/dextertest/Source;
    goto :goto_1b

    .line 102
    :pswitch_79
    new-instance v3, Luk/ac/cam/db538/dextertest/Source_DeviceID;

    .end local v3           #source:Luk/ac/cam/db538/dextertest/Source;
    invoke-direct {v3}, Luk/ac/cam/db538/dextertest/Source_DeviceID;-><init>()V

    .restart local v3       #source:Luk/ac/cam/db538/dextertest/Source;
    goto :goto_1b

    .line 110
    :pswitch_7f
    new-instance v2, Luk/ac/cam/db538/dextertest/Sink_ApacheHTTPClient;

    .end local v2           #sink:Luk/ac/cam/db538/dextertest/Sink;
    invoke-direct {v2}, Luk/ac/cam/db538/dextertest/Sink_ApacheHTTPClient;-><init>()V

    .line 111
    .restart local v2       #sink:Luk/ac/cam/db538/dextertest/Sink;
    goto :goto_34

    .line 113
    :pswitch_85
    new-instance v2, Luk/ac/cam/db538/dextertest/Sink_Socket;

    .end local v2           #sink:Luk/ac/cam/db538/dextertest/Sink;
    invoke-direct {v2}, Luk/ac/cam/db538/dextertest/Sink_Socket;-><init>()V

    .line 114
    .restart local v2       #sink:Luk/ac/cam/db538/dextertest/Sink;
    goto :goto_34

    .line 116
    :pswitch_8b
    new-instance v2, Luk/ac/cam/db538/dextertest/Sink_Log;

    .end local v2           #sink:Luk/ac/cam/db538/dextertest/Sink;
    invoke-direct {v2}, Luk/ac/cam/db538/dextertest/Sink_Log;-><init>()V

    .line 117
    .restart local v2       #sink:Luk/ac/cam/db538/dextertest/Sink;
    goto :goto_34

    .line 119
    :pswitch_91
    new-instance v2, Luk/ac/cam/db538/dextertest/Sink_FileSystem;

    .end local v2           #sink:Luk/ac/cam/db538/dextertest/Sink;
    invoke-direct {v2}, Luk/ac/cam/db538/dextertest/Sink_FileSystem;-><init>()V

    .line 120
    .restart local v2       #sink:Luk/ac/cam/db538/dextertest/Sink;
    goto :goto_34

    .line 122
    :pswitch_97
    new-instance v2, Luk/ac/cam/db538/dextertest/Sink_IPC;

    .end local v2           #sink:Luk/ac/cam/db538/dextertest/Sink;
    invoke-direct {v2}, Luk/ac/cam/db538/dextertest/Sink_IPC;-><init>()V

    .restart local v2       #sink:Luk/ac/cam/db538/dextertest/Sink;
    goto :goto_34

    .line 133
    :catch_9d
    move-exception v1

    .line 134
    .local v1, e:Ljava/lang/RuntimeException;
    invoke-virtual {v1}, Ljava/lang/RuntimeException;->printStackTrace()V

    .line 135
    iget-object v4, p0, Luk/ac/cam/db538/dextertest/MainActivity$1$2;->val$threadHandler:Landroid/os/Handler;

    const/4 v5, 0x1

    invoke-virtual {v4, v5}, Landroid/os/Handler;->sendEmptyMessage(I)Z

    goto :goto_5a

    .line 85
    :pswitch_data_a8
    .packed-switch 0x0
        :pswitch_5b
        :pswitch_61
        :pswitch_67
        :pswitch_6d
        :pswitch_73
        :pswitch_79
    .end packed-switch

    .line 108
    :pswitch_data_b8
    .packed-switch 0x0
        :pswitch_7f
        :pswitch_85
        :pswitch_8b
        :pswitch_91
        :pswitch_97
    .end packed-switch
.end method
