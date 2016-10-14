package test;

/**
 * Created by 俊帆 on 2016/10/14.
 */
@com.opdar.gulosity.spring.annotations.Table("nursing.t2")
public class Table {
    private String a;
    private String b;
    private String c;

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("Table{");
        sb.append("a='").append(a).append('\'');
        sb.append(", b='").append(b).append('\'');
        sb.append(", c='").append(c).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
