.class public Luk/ac/cam/db538/dextertest/MainActivity;
.super Landroid/app/Activity;
.source "MainActivity.java"


# instance fields
.field private buttonTest:Landroid/widget/Button;

.field private final buttonTest_onClick:Landroid/view/View$OnClickListener;

.field private spinnerSink:Landroid/widget/Spinner;

.field private spinnerSource:Landroid/widget/Spinner;


# direct methods
.method public constructor <init>()V
    .registers 2

    .prologue
    .line 17
    invoke-direct {p0}, Landroid/app/Activity;-><init>()V

    .line 42
    new-instance v0, Luk/ac/cam/db538/dextertest/MainActivity$1;

    invoke-direct {v0, p0}, Luk/ac/cam/db538/dextertest/MainActivity$1;-><init>(Luk/ac/cam/db538/dextertest/MainActivity;)V

    iput-object v0, p0, Luk/ac/cam/db538/dextertest/MainActivity;->buttonTest_onClick:Landroid/view/View$OnClickListener;

    .line 17
    return-void
.end method

.method static synthetic access$0(Luk/ac/cam/db538/dextertest/MainActivity;)Landroid/widget/Spinner;
    .registers 2
    .parameter

    .prologue
    .line 20
    iget-object v0, p0, Luk/ac/cam/db538/dextertest/MainActivity;->spinnerSource:Landroid/widget/Spinner;

    return-object v0
.end method

.method static synthetic access$1(Luk/ac/cam/db538/dextertest/MainActivity;)Landroid/widget/Spinner;
    .registers 2
    .parameter

    .prologue
    .line 21
    iget-object v0, p0, Luk/ac/cam/db538/dextertest/MainActivity;->spinnerSink:Landroid/widget/Spinner;

    return-object v0
.end method


# virtual methods
.method protected onCreate(Landroid/os/Bundle;)V
    .registers 4
    .parameter "savedInstanceState"

    .prologue
    .line 25
    invoke-super {p0, p1}, Landroid/app/Activity;->onCreate(Landroid/os/Bundle;)V

    .line 26
    const/high16 v0, 0x7f03

    invoke-virtual {p0, v0}, Luk/ac/cam/db538/dextertest/MainActivity;->setContentView(I)V

    .line 28
    const v0, 0x7f080006

    invoke-virtual {p0, v0}, Luk/ac/cam/db538/dextertest/MainActivity;->findViewById(I)Landroid/view/View;

    move-result-object v0

    check-cast v0, Landroid/widget/Button;

    iput-object v0, p0, Luk/ac/cam/db538/dextertest/MainActivity;->buttonTest:Landroid/widget/Button;

    .line 29
    iget-object v0, p0, Luk/ac/cam/db538/dextertest/MainActivity;->buttonTest:Landroid/widget/Button;

    iget-object v1, p0, Luk/ac/cam/db538/dextertest/MainActivity;->buttonTest_onClick:Landroid/view/View$OnClickListener;

    invoke-virtual {v0, v1}, Landroid/widget/Button;->setOnClickListener(Landroid/view/View$OnClickListener;)V

    .line 31
    const v0, 0x7f080002

    invoke-virtual {p0, v0}, Luk/ac/cam/db538/dextertest/MainActivity;->findViewById(I)Landroid/view/View;

    move-result-object v0

    check-cast v0, Landroid/widget/Spinner;

    iput-object v0, p0, Luk/ac/cam/db538/dextertest/MainActivity;->spinnerSource:Landroid/widget/Spinner;

    .line 32
    const v0, 0x7f080005

    invoke-virtual {p0, v0}, Luk/ac/cam/db538/dextertest/MainActivity;->findViewById(I)Landroid/view/View;

    move-result-object v0

    check-cast v0, Landroid/widget/Spinner;

    iput-object v0, p0, Luk/ac/cam/db538/dextertest/MainActivity;->spinnerSink:Landroid/widget/Spinner;

    .line 33
    return-void
.end method

.method public onCreateOptionsMenu(Landroid/view/Menu;)Z
    .registers 4
    .parameter "menu"

    .prologue
    .line 38
    invoke-virtual {p0}, Luk/ac/cam/db538/dextertest/MainActivity;->getMenuInflater()Landroid/view/MenuInflater;

    move-result-object v0

    const/high16 v1, 0x7f07

    invoke-virtual {v0, v1, p1}, Landroid/view/MenuInflater;->inflate(ILandroid/view/Menu;)V

    .line 39
    const/4 v0, 0x1

    return v0
.end method

.method protected onResume()V
    .registers 3

    .prologue
    .line 152
    invoke-super {p0}, Landroid/app/Activity;->onResume()V

    .line 153
    invoke-static {}, Ljava/lang/System;->nanoTime()J

    move-result-wide v0

    sput-wide v0, Luk/ac/cam/db538/dextertest/MyApplication;->Timer_ButtonClick:J

    .line 154
    iget-object v0, p0, Luk/ac/cam/db538/dextertest/MainActivity;->spinnerSource:Landroid/widget/Spinner;

    const/4 v1, 0x3

    invoke-virtual {v0, v1}, Landroid/widget/Spinner;->setSelection(I)V

    .line 155
    iget-object v0, p0, Luk/ac/cam/db538/dextertest/MainActivity;->spinnerSink:Landroid/widget/Spinner;

    const/4 v1, 0x2

    invoke-virtual {v0, v1}, Landroid/widget/Spinner;->setSelection(I)V

    .line 156
    iget-object v0, p0, Luk/ac/cam/db538/dextertest/MainActivity;->buttonTest:Landroid/widget/Button;

    invoke-virtual {v0}, Landroid/widget/Button;->performClick()Z

    .line 157
    return-void
.end method

.method protected onStart()V
    .registers 7

    .prologue
    .line 145
    invoke-super {p0}, Landroid/app/Activity;->onStart()V

    .line 146
    invoke-static {}, Ljava/lang/System;->nanoTime()J

    move-result-wide v2

    sget-wide v4, Luk/ac/cam/db538/dextertest/MyApplication;->Timer_AppInit:J

    sub-long v0, v2, v4

    .line 147
    .local v0, timeAppLaunch:J
    const-string v2, "TIMER_APPINIT"

    invoke-static {v0, v1}, Ljava/lang/Long;->toString(J)Ljava/lang/String;

    move-result-object v3

    invoke-static {v2, v3}, Landroid/util/Log;->d(Ljava/lang/String;Ljava/lang/String;)I

    .line 148
    return-void
.end method
