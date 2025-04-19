package appnghenhac.com;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import java.util.ArrayList;

public class UpgradeRequestAdapterActivity extends BaseAdapter {
    private Context context;
    private ArrayList<User> users;
    private DatabaseHelper dbHelper;

    public UpgradeRequestAdapterActivity(Context context, ArrayList<User> users) {
        this.context = context;
        this.users = users;
        this.dbHelper = new DatabaseHelper(context);
    }

    @Override
    public int getCount() {
        return users.size();
    }

    @Override
    public Object getItem(int position) {
        return users.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(R.layout.item_upgrade_request, parent, false);
        }

        User user = users.get(position);

        TextView tvName = convertView.findViewById(R.id.tvUserName);
        TextView tvEmail = convertView.findViewById(R.id.tvUserEmail);
        Button btnApprove = convertView.findViewById(R.id.btnApprove);
        Button btnReject = convertView.findViewById(R.id.btnReject);

        tvName.setText(user.getName());
        tvEmail.setText(user.getEmail());

        btnApprove.setOnClickListener(v -> {
            boolean success = dbHelper.updateUserRole(user.getEmail(), "PREMIUM");
            if (success) {
                Toast.makeText(context, "Yêu cầu nâng cấp đã được duyệt!", Toast.LENGTH_SHORT).show();
                users.remove(position);
                notifyDataSetChanged();
            } else {
                Toast.makeText(context, "Lỗi khi duyệt yêu cầu!", Toast.LENGTH_SHORT).show();
            }
        });

        btnReject.setOnClickListener(v -> {
            boolean success = dbHelper.updateUpgradeRequestStatus(user.getEmail(), "rejected");
            if (success) {
                Toast.makeText(context, "Yêu cầu nâng cấp đã bị từ chối!", Toast.LENGTH_SHORT).show();
                users.remove(position);
                notifyDataSetChanged();
            } else {
                Toast.makeText(context, "Lỗi khi từ chối yêu cầu!", Toast.LENGTH_SHORT).show();
            }
        });

        return convertView;
    }
}