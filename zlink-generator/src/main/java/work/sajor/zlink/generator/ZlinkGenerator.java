package work.sajor.zlink.generator;

import work.sajor.crap.generator.CrapGenerator;

/**
 * <p>
 * 代码生成
 * </p>
 *
 * @author Sajor
 * @since 2022-11-17
 */
public class ZlinkGenerator extends CrapGenerator {

    public static void main(String[] args) {
        new ZlinkGenerator().run();
    }

    @Override
    public void run() {
        generate(
                "work.sajor.zlink.common.dao",
                "/zlink-common",
                "job_",
                "job_",
                new String[]{},
                new String[]{},
                false
        );
    }
}
