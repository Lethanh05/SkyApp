package com.example.skymall.ui.voucher;

import android.content.Context;

import com.example.skymall.data.remote.DTO.VoucherCheckResp;
import com.example.skymall.data.remote.DTO.VoucherUseResp;
import com.example.skymall.data.repository.VoucherRepository;

/**
 * Manager class để quản lý voucher trong quá trình checkout
 */
public class VoucherManager {

    public interface VoucherApplyListener {
        void onVoucherApplied(VoucherCheckResp.VoucherCheckData voucher);
        void onVoucherRemoved();
        void onVoucherUsed(String message);
        void onError(String error);
    }

    private Context context;
    private VoucherRepository voucherRepository;
    private VoucherCheckResp.VoucherCheckData currentVoucher;
    private VoucherApplyListener listener;

    public VoucherManager(Context context, VoucherApplyListener listener) {
        this.context = context;
        this.listener = listener;
        this.voucherRepository = new VoucherRepository(context);
    }

    /**
     * Kiểm tra và áp dụng voucher
     */
    public void checkAndApplyVoucher(String voucherCode, double orderValue) {
        voucherRepository.checkVoucher(voucherCode, orderValue, new VoucherRepository.VoucherCheckCallback() {
            @Override
            public void onSuccess(VoucherCheckResp response) {
                if (response.voucher != null) {
                    currentVoucher = response.voucher;
                    if (listener != null) {
                        listener.onVoucherApplied(response.voucher);
                    }
                }
            }

            @Override
            public void onError(String error, String[] details) {
                String errorMessage = VoucherUtils.getErrorMessage(context, error);
                if (details.length > 0) {
                    errorMessage += "\\n" + VoucherUtils.formatErrorMessages(context, details);
                }
                if (listener != null) {
                    listener.onError(errorMessage);
                }
            }
        });
    }

    /**
     * Sử dụng voucher đã được áp dụng cho đơn hàng
     */
    public void useVoucherForOrder(int orderId) {
        if (currentVoucher == null) {
            if (listener != null) {
                listener.onError("Không có voucher nào được áp dụng");
            }
            return;
        }

        voucherRepository.useVoucher(currentVoucher.id, orderId, new VoucherRepository.VoucherUseCallback() {
            @Override
            public void onSuccess(VoucherUseResp response) {
                if (listener != null) {
                    listener.onVoucherUsed(response.message != null ? response.message : "Voucher đã được sử dụng thành công");
                }
                // Clear current voucher after successful use
                currentVoucher = null;
            }

            @Override
            public void onError(String error) {
                String errorMessage = VoucherUtils.getErrorMessage(context, error);
                if (listener != null) {
                    listener.onError(errorMessage);
                }
            }
        });
    }

    /**
     * Bỏ áp dụng voucher hiện tại
     */
    public void removeVoucher() {
        currentVoucher = null;
        if (listener != null) {
            listener.onVoucherRemoved();
        }
    }

    /**
     * Lấy voucher hiện tại được áp dụng
     */
    public VoucherCheckResp.VoucherCheckData getCurrentVoucher() {
        return currentVoucher;
    }

    /**
     * Kiểm tra có voucher được áp dụng không
     */
    public boolean hasVoucherApplied() {
        return currentVoucher != null;
    }

    /**
     * Lấy số tiền giảm giá của voucher hiện tại
     */
    public double getCurrentDiscountAmount() {
        return currentVoucher != null ? currentVoucher.discountAmount : 0;
    }

    /**
     * Hiển thị dialog để chọn/kiểm tra voucher
     */
    public void showVoucherDialog(double orderValue) {
        VoucherCheckDialog dialog = new VoucherCheckDialog(context, orderValue, new VoucherCheckDialog.OnVoucherAppliedListener() {
            @Override
            public void onVoucherApplied(VoucherCheckResp.VoucherCheckData voucher) {
                currentVoucher = voucher;
                if (listener != null) {
                    listener.onVoucherApplied(voucher);
                }
            }

            @Override
            public void onVoucherRemoved() {
                removeVoucher();
            }
        });
        dialog.show();
    }
}
