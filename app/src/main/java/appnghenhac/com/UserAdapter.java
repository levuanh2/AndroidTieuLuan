package appnghenhac.com;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.TextView;
import java.util.ArrayList;

public class UserAdapter extends BaseAdapter {
    private Context context;
    private ArrayList<User> users;
    private User selectedUser;

    public UserAdapter(Context context, ArrayList<User> users) {
        this.context = context;
        this.users = users;
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
            convertView = LayoutInflater.from(context).inflate(R.layout.item_user, parent, false);
        }

        User user = users.get(position);

        TextView tvName = convertView.findViewById(R.id.tvUserName);
        TextView tvEmail = convertView.findViewById(R.id.tvUserEmail);
        CheckBox cbSelect = convertView.findViewById(R.id.cbSelect);

        tvName.setText(user.getName());
        tvEmail.setText(user.getEmail());
        cbSelect.setChecked(user.isSelected());

        cbSelect.setOnCheckedChangeListener((buttonView, isChecked) -> {
            user.setSelected(isChecked);
            if (isChecked) {
                // Đảm bảo chỉ một người dùng được chọn
                for (User u : users) {
                    if (u != user) u.setSelected(false);
                }
                selectedUser = user;
            } else {
                selectedUser = null;
            }
            notifyDataSetChanged();
        });

        return convertView;
    }

    public User getSelectedUser() {
        return selectedUser;
    }

    public void updateUsers(ArrayList<User> newUsers) {
        this.users = newUsers;
        selectedUser = null;
        notifyDataSetChanged();
    }
}