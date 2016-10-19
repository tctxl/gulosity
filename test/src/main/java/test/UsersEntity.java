package test;

import com.opdar.gulosity.spring.annotations.Table;
import com.opdar.gulosity.spring.annotations.TableField;

/**
 * Created by 俊帆 on 2016/10/14.
 */
@Table(value = "nursing.nurs_users",camelCase = true,extension = Table.CaseExtension.NULL)
public class UsersEntity {
    private String id;
    private String userName;
    @TableField("user_pwd")
    private String password;

    @Override
    public String toString() {
        return "UsersEntity{" + "id='" + id + '\'' +
                ", userName='" + userName + '\'' +
                ", password='" + password + '\'' +
                '}';
    }
}
