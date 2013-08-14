.class public abstract Luk/ac/cam/db538/dexter/tests/MyClass_Binder;
.super Landroid/os/Binder;

# direct methods
.method public constructor <init>()V
    .registers 2

    invoke-direct {p0}, Landroid/os/Binder;-><init>()V
    return-void
    
.end method

.method protected onTransact(ILandroid/os/Parcel;Landroid/os/Parcel;I)Z
	.registers 5

	rem-int/lit8 v0, p1, 1
	return v0

.end method

.method public static exec(Landroid/os/Binder;I)I
	.registers 4

	# create a Parcel
	invoke-static { }, Landroid/os/Parcel;->obtain()Landroid/os/Parcel;
	move-result v0

	invoke-virtual {p0, p1, v0, v0, p1}, Landroid/os/Binder;->onTransact(ILandroid/os/Parcel;Landroid/os/Parcel;I)Z
	move-result v1

	return v1

.end method