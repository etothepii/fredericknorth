package uk.co.epii.conservatives.fredericknorth.geometry;

import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import uk.co.epii.conservatives.fredericknorth.geometry.extensions.PolygonExtensions;
import uk.co.epii.conservatives.fredericknorth.geometry.extensions.RectangleExtensions;

import java.awt.*;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;

/**
 * User: James Robinson
 * Date: 23/02/2014
 * Time: 02:16
 */
public class SquareSearchPolygonSifterImplTests {

    private static final Logger LOG = LoggerFactory.getLogger(SquareSearchPolygonSifterImplTests.class);

    private static int[] th_x = { 536860, 536854, 536843, 536837, 536836, 536835, 536835, 536837, 536838, 536842, 536841, 536840, 536845, 536847, 536849, 536858, 536864, 536869, 536870, 536870, 536872, 536873, 536872, 536866, 536858, 536853, 536849, 536850, 536852, 536853, 536852, 536843, 536834, 536828, 536822, 536815, 536798, 536785, 536778, 536777, 536774, 536766, 536761, 536746, 536729, 536719, 536710, 536703, 536697, 536692, 536664, 536648, 536624, 536604, 536570, 536514, 536469, 536426, 536399, 536347, 536309, 536293, 536266, 536243, 536197, 536144, 536101, 536077, 536053, 536025, 535972, 535929, 535886, 535867, 535845, 535818, 535762, 535738, 535705, 535677, 535665, 535639, 535558, 535524, 535470, 535336, 535309, 535280, 535269, 535244, 535185, 535103, 535103, 535083, 535054, 535027, 535000, 535000, 534959, 534942, 534909, 534868, 534832, 534783, 534758, 534746, 534736, 534719, 534687, 534583, 534534, 534498, 534455, 534425, 534337, 534318, 534294, 534264, 534211, 534211, 534197, 534176, 534111, 534060, 534043, 534035, 534017, 534012, 533985, 533960, 533920, 533858, 533824, 533785, 533764, 533681, 533604, 533541, 533480, 533472, 533470, 533452, 533421, 533399, 533360, 533348, 533334, 533360, 533362, 533376, 533386, 533387, 533389, 533396, 533405, 533408, 533409, 533420, 533429, 533430, 533429, 533425, 533428, 533404, 533403, 533406, 533412, 533417, 533440, 533442, 533443, 533450, 533465, 533479, 533491, 533505, 533540, 533551, 533553, 533553, 533552, 533552, 533575, 533594, 533595, 533602, 533604, 533609, 533630, 533631, 533632, 533637, 533637, 533641, 533642, 533645, 533646, 533657, 533696, 533711, 533725, 533740, 533751, 533765, 533787, 533813, 533824, 533829, 533834, 533838, 533839, 533839, 533838, 533837, 533832, 533826, 533820, 533816, 533814, 533807, 533796, 533787, 533786, 533779, 533765, 533743, 533719, 533709, 533683, 533659, 533567, 533539, 533536, 533535, 533532, 533499, 533496, 533478, 533475, 533472, 533467, 533455, 533442, 533448, 533445, 533427, 533352, 533368, 533378, 533387, 533387, 533389, 533394, 533410, 533411, 533426, 533447, 533501, 533530, 533549, 533567, 533568, 533574, 533578, 533587, 533589, 533590, 533593, 533595, 533595, 533590, 533579, 533543, 533532, 533528, 533525, 533524, 533523, 533523, 533530, 533541, 533543, 533478, 533503, 533539, 533560, 533584, 533587, 533597, 533608, 533633, 533651, 533670, 533683, 533692, 533710, 533753, 533768, 533778, 533813, 533837, 533857, 533885, 533933, 533976, 534019, 534109, 534156, 534225, 534234, 534252, 534269, 534286, 534301, 534303, 534313, 534431, 534472, 534502, 534496, 534486, 534477, 534466, 534463, 534456, 534454, 534458, 534466, 534479, 534505, 534533, 534559, 534588, 534606, 534608, 534622, 534628, 534640, 534661, 534676, 534699, 534720, 534741, 534785, 534823, 534841, 534874, 534915, 534954, 534969, 534981, 534982, 534983, 534996, 535008, 535019, 535040, 535058, 535078, 535105, 535136, 535149, 535150, 535156, 535164, 535177, 535194, 535211, 535219, 535230, 535242, 535275, 535285, 535296, 535306, 535316, 535324, 535347, 535367, 535373, 535390, 535419, 535441, 535446, 535452, 535461, 535469, 535477, 535483, 535493, 535512, 535529, 535541, 535557, 535622, 535695, 535699, 535704, 535710, 535714, 535737, 535759, 535769, 535776, 535787, 535794, 535867, 535932, 535954, 535979, 536017, 536087, 536117, 536164, 536190, 536191, 536196, 536202, 536210, 536216, 536217, 536174, 536240, 536301, 536350, 536392, 536423, 536436, 536443, 536451, 536466, 536469, 536477, 536483, 536491, 536501, 536567, 536601, 536633, 536641, 536672, 536713, 536732, 536745, 536751, 536768, 536779, 536780, 536799, 536822, 536859, 536891, 536914, 536951, 536995, 537002, 537006, 537009, 537012, 537017, 537019, 537030, 537133, 537188, 537187, 537192, 537210, 537224, 537236, 537237, 537241, 537299, 537376, 537408, 537454, 537500, 537554, 537579, 537605, 537621, 537625, 537639, 537639, 537641, 537644, 537648, 537658, 537674, 537673, 537668, 537667, 537658, 537653, 537648, 537640, 537630, 537615, 537599, 537584, 537573, 537564, 537561, 537564, 537562, 537557, 537553, 537542, 537529, 537506, 537495, 537488, 537481, 537474, 537468, 537456, 537451, 537451, 537450, 537439, 537433, 537424, 537417, 537403, 537379, 537376, 537361, 537343, 537333, 537332, 537334, 537336, 537339, 537340, 537354, 537362, 537381, 537392, 537413, 537429, 537445, 537455, 537471, 537484, 537490, 537494, 537495, 537497, 537501, 537507, 537522, 537543, 537556, 537561, 537571, 537571, 537572, 537570, 537568, 537559, 537555, 537556, 537559, 537571, 537589, 537606, 537613, 537614, 537632, 537655, 537683, 537750, 537789, 537816, 537837, 537845, 537851, 537852, 537854, 537860, 537882, 537915, 537950, 538006, 538055, 538072, 538082, 538093, 538098, 538104, 538109, 538115, 538125, 538136, 538180, 538216, 538230, 538240, 538250, 538257, 538267, 538280, 538301, 538310, 538318, 538322, 538320, 538318, 538308, 538302, 538299, 538295, 538290, 538285, 538284, 538282, 538283, 538286, 538304, 538306, 538308, 538301, 538294, 538285, 538281, 538266, 538264, 538263, 538253, 538238, 538229, 538228, 538231, 538240, 538245, 538263, 538311, 538324, 538337, 538363, 538378, 538390, 538395, 538421, 538447, 538457, 538463, 538466, 538467, 538469, 538471, 538474, 538479, 538496, 538500, 538510, 538517, 538522, 538532, 538544, 538549, 538553, 538558, 538575, 538581, 538589, 538602, 538609, 538612, 538624, 538639, 538689, 538696, 538701, 538706, 538711, 538721, 538765, 538821, 538909, 538989, 539006, 539031, 539048, 539056, 539079, 539093, 539124, 539138, 539144, 539152, 539155, 539160, 539159, 539157, 539155, 539152, 539141, 539128, 539121, 539108, 539074, 539056, 539048, 539045, 539042, 539040, 539040, 539043, 539047, 539052, 539057, 539061, 539067, 539100, 539115, 539133, 539145, 539156, 539160, 539165, 539170, 539182, 539186, 539190, 539193, 539198, 539204, 539206, 539202, 539201, 539204, 539209, 539213, 539217, 539219, 539230, 539242, 539257, 539262, 539300, 539311, 539320, 539337, 539355, 539369, 539382, 539387, 539393, 539401, 539407, 539423, 539426, 539427, 539434, 539437, 539435, 539427, 539416, 539413, 539408, 539397, 539388, 539383, 539365, 539347, 539318, 539316, 539316, 539312, 539305, 539301, 539301, 539305, 539316, 539323, 539340, 539361, 539394, 539422, 539424, 539434, 539450, 539478, 539505, 539520, 539531, 539545, 539558, 539564, 539569, 539575, 539579, 539579, 539576, 539565, 539557, 539546, 539535, 539527, 539508, 539475, 539430, 539410, 539315, 539259, 539187, 539173, 539168, 539149, 539118, 539086, 539050, 538994, 538966, 538945, 538910, 538889, 538837, 538810, 538764, 538733, 538720, 538705, 538694, 538682, 538673, 538660, 538650, 538628, 538609, 538597, 538594, 538588, 538578, 538576, 538571, 538565, 538562, 538558, 538556, 538557, 538557, 538557, 538557, 538564, 538572, 538576, 538603, 538613, 538621, 538624, 538637, 538649, 538674, 538737, 538763, 538775, 538782, 538787, 538789, 538796, 538826, 538838, 538855, 538865, 538869, 538886, 538896, 538917, 538916, 538917, 538916, 538914, 538910, 538902, 538887, 538872, 538852, 538835, 538806, 538753, 538722, 538645, 538632, 538605, 538588, 538577, 538570, 538551, 538529, 538496, 538480, 538446, 538412, 538229, 538214, 538198, 538079, 538072, 538051, 538008, 537956, 537939, 537916, 537892, 537879, 537860, 537830, 537817, 537814, 537800, 537756, 537754, 537740, 537714, 537659, 537648, 537612, 537574, 537499, 537477, 537442, 537362, 537332, 537327, 537324, 537305, 537282, 537264, 537243, 537232, 537212, 537151, 537107, 537092, 537070, 537057, 537040, 537017, 536993, 536977, 536931, 536907, 536885, 536874, 536871, 536860 };
    private static int[] th_y = { 179019, 179066, 179142, 179216, 179233, 179261, 179297, 179408, 179417, 179462, 179518, 179558, 179618, 179654, 179684, 179751, 179860, 179969, 179997, 180012, 180028, 180035, 180040, 180073, 180124, 180160, 180187, 180208, 180220, 180222, 180225, 180251, 180281, 180302, 180316, 180336, 180365, 180383, 180395, 180397, 180403, 180412, 180417, 180427, 180434, 180443, 180451, 180460, 180469, 180472, 180496, 180509, 180517, 180525, 180537, 180563, 180584, 180608, 180622, 180637, 180652, 180658, 180665, 180671, 180675, 180676, 180675, 180676, 180675, 180673, 180667, 180661, 180653, 180648, 180640, 180628, 180602, 180588, 180562, 180538, 180528, 180507, 180433, 180400, 180348, 180215, 180191, 180166, 180157, 180132, 180074, 180000, 180000, 179986, 179967, 179953, 179942, 179942, 179924, 179916, 179905, 179893, 179883, 179871, 179865, 179864, 179865, 179867, 179868, 179888, 179896, 179903, 179913, 179922, 179949, 179956, 179967, 179977, 180000, 180000, 180002, 180011, 180037, 180060, 180068, 180072, 180082, 180085, 180101, 180120, 180147, 180188, 180206, 180223, 180230, 180258, 180291, 180319, 180344, 180347, 180348, 180356, 180368, 180376, 180394, 180399, 180406, 180486, 180488, 180503, 180510, 180516, 180523, 180522, 180558, 180571, 180580, 180607, 180625, 180627, 180627, 180629, 180639, 180651, 180652, 180658, 180659, 180691, 180695, 180696, 180703, 180719, 180745, 180761, 180771, 180778, 180783, 180784, 180789, 180798, 180813, 180816, 180819, 180822, 180812, 180812, 180813, 180774, 180777, 180771, 180750, 180747, 180746, 180741, 180739, 180735, 180736, 180741, 180763, 180761, 180759, 180766, 180771, 180776, 180779, 180779, 180779, 180784, 180794, 180801, 180808, 180839, 180857, 180865, 180888, 180907, 180928, 180954, 180968, 181027, 181094, 181142, 181142, 181175, 181226, 181261, 181286, 181298, 181331, 181359, 181457, 181494, 181498, 181501, 181506, 181582, 181587, 181634, 181646, 181653, 181660, 181676, 181700, 181702, 181755, 181752, 181739, 181807, 181881, 181941, 181942, 181951, 181985, 182037, 182041, 182076, 182086, 182107, 182119, 182097, 182113, 182118, 182138, 182158, 182196, 182204, 182212, 182228, 182262, 182273, 182303, 182355, 182350, 182466, 182510, 182539, 182557, 182571, 182583, 182597, 182638, 182668, 182683, 182707, 182749, 182777, 182808, 182813, 182831, 182854, 182923, 182965, 182990, 183005, 183013, 183028, 183055, 183064, 183069, 183086, 183091, 183097, 183101, 183108, 183111, 183113, 183122, 183129, 183140, 183171, 183229, 183279, 183332, 183364, 183359, 183329, 183352, 183365, 183372, 183388, 183413, 183432, 183466, 183481, 183526, 183565, 183577, 183600, 183624, 183618, 183600, 183571, 183534, 183509, 183507, 183496, 183492, 183486, 183478, 183475, 183472, 183472, 183475, 183486, 183503, 183513, 183533, 183558, 183578, 183585, 183590, 183590, 183590, 183593, 183594, 183599, 183601, 183600, 183598, 183593, 183581, 183577, 183604, 183644, 183665, 183687, 183706, 183722, 183728, 183735, 183741, 183750, 183752, 183753, 183753, 183752, 183751, 183745, 183739, 183736, 183724, 183702, 183679, 183675, 183670, 183665, 183662, 183660, 183659, 183659, 183653, 183651, 183650, 183650, 183664, 183687, 183689, 183692, 183696, 183700, 183727, 183759, 183766, 183768, 183764, 183769, 183826, 183879, 183914, 183952, 184019, 184126, 184177, 184253, 184296, 184304, 184309, 184317, 184319, 184328, 184329, 184368, 184444, 184507, 184547, 184581, 184626, 184644, 184655, 184668, 184688, 184691, 184697, 184698, 184697, 184692, 184633, 184602, 184575, 184568, 184558, 184543, 184532, 184521, 184504, 184470, 184440, 184438, 184445, 184453, 184469, 184484, 184494, 184512, 184538, 184542, 184547, 184552, 184565, 184563, 184555, 184551, 184564, 184568, 184572, 184573, 184574, 184575, 184576, 184578, 184585, 184590, 184600, 184601, 184603, 184601, 184591, 184588, 184585, 184580, 184585, 184582, 184576, 184563, 184550, 184542, 184523, 184491, 184480, 184471, 184469, 184447, 184404, 184376, 184361, 184348, 184341, 184333, 184326, 184319, 184310, 184295, 184264, 184240, 184226, 184214, 184197, 184180, 184158, 184144, 184132, 184106, 184066, 184041, 184012, 184008, 184007, 184006, 183994, 183988, 183980, 183975, 183966, 183948, 183945, 183929, 183905, 183881, 183868, 183857, 183850, 183830, 183813, 183788, 183779, 183771, 183766, 183755, 183736, 183717, 183697, 183669, 183641, 183620, 183604, 183578, 183561, 183550, 183540, 183523, 183504, 183488, 183480, 183457, 183456, 183436, 183428, 183423, 183401, 183383, 183366, 183356, 183341, 183313, 183270, 183261, 183260, 183242, 183223, 183207, 183170, 183156, 183144, 183130, 183119, 183107, 183104, 183101, 183094, 183084, 183069, 183063, 183055, 183046, 183040, 183031, 183018, 183003, 182975, 182956, 182945, 182930, 182921, 182883, 182857, 182844, 182830, 182803, 182779, 182760, 182739, 182718, 182705, 182691, 182668, 182644, 182628, 182609, 182599, 182595, 182579, 182551, 182459, 182425, 182399, 182380, 182370, 182335, 182332, 182316, 182285, 182264, 182253, 182233, 182181, 182175, 182173, 182160, 182120, 182082, 182058, 182029, 181986, 181975, 181960, 181930, 181921, 181911, 181892, 181877, 181863, 181855, 181814, 181761, 181739, 181718, 181664, 181658, 181653, 181648, 181644, 181639, 181626, 181625, 181623, 181623, 181623, 181626, 181632, 181636, 181639, 181646, 181678, 181688, 181712, 181744, 181753, 181757, 181765, 181773, 181785, 181786, 181785, 181785, 181783, 181778, 181742, 181700, 181637, 181596, 181587, 181572, 181562, 181556, 181540, 181529, 181497, 181481, 181471, 181455, 181446, 181427, 181410, 181399, 181390, 181381, 181353, 181319, 181297, 181255, 181166, 181108, 181080, 181067, 181050, 181041, 181021, 181006, 180996, 180986, 180981, 180979, 180976, 180970, 180970, 180971, 180973, 180978, 180982, 180985, 180991, 181012, 181021, 181034, 181051, 181076, 181126, 181158, 181193, 181223, 181257, 181278, 181293, 181300, 181312, 181334, 181347, 181373, 181378, 181397, 181397, 181399, 181399, 181396, 181392, 181383, 181375, 181363, 181348, 181336, 181316, 181307, 181292, 181278, 181251, 181220, 181175, 181160, 181152, 181142, 181121, 181112, 181091, 181041, 181020, 180981, 180974, 180967, 180961, 180940, 180923, 180913, 180902, 180890, 180884, 180875, 180867, 180860, 180856, 180855, 180855, 180850, 180841, 180830, 180821, 180813, 180798, 180779, 180770, 180760, 180748, 180736, 180723, 180709, 180673, 180658, 180619, 180585, 180567, 180539, 180491, 180419, 180431, 180488, 180516, 180546, 180549, 180550, 180551, 180548, 180546, 180545, 180535, 180524, 180517, 180503, 180494, 180463, 180442, 180394, 180360, 180339, 180322, 180304, 180284, 180264, 180242, 180220, 180186, 180143, 180110, 180095, 180062, 180017, 180000, 179985, 179968, 179957, 179944, 179920, 179901, 179895, 179887, 179880, 179863, 179823, 179797, 179672, 179631, 179603, 179591, 179553, 179519, 179464, 179304, 179243, 179220, 179206, 179197, 179194, 179182, 179131, 179106, 179065, 179046, 179019, 178962, 178918, 178833, 178780, 178752, 178712, 178687, 178661, 178626, 178579, 178534, 178488, 178455, 178413, 178356, 178328, 178262, 178254, 178234, 178221, 178214, 178210, 178199, 178189, 178175, 178167, 178149, 178131, 178074, 178070, 178067, 178047, 178044, 178043, 178044, 178046, 178046, 178047, 178047, 178048, 178051, 178052, 178055, 178055, 178058, 178066, 178066, 178065, 178070, 178093, 178097, 178111, 178129, 178170, 178182, 178203, 178255, 178278, 178282, 178284, 178299, 178319, 178335, 178353, 178365, 178386, 178452, 178502, 178520, 178550, 178565, 178595, 178633, 178682, 178715, 178806, 178867, 178922, 178949, 178960, 179019};
    private static int th_n = 947;
    Polygon[] polygons = new Polygon[] {new Polygon(th_x, th_y, th_n)};
    int tests = 10000;

    @Test
    public void singleTest1() {
        Point outside = new Point(539207,181445);
        SquareSearchPolygonSifterImpl squareSearch = new SquareSearchPolygonSifterImpl(polygons, tests);
        boolean result = squareSearch.contains(outside);
        assertFalse(result);
    }

    @Test
    public void compareWithBruteForce() {
        Rectangle rectangle = PolygonExtensions.getBounds(polygons);
        rectangle = RectangleExtensions.grow(rectangle, Math.max(rectangle.width / 10, rectangle.height / 10));
        Point[] points = new Point[tests];
        for (int i = 0; i < tests; i++) {
            points[i] = new Point(rectangle.x + (int)(Math.random() * rectangle.width),
                    rectangle.y + (int)(Math.random() * rectangle.height));
        }
        boolean[] bruteForceResults = new boolean[tests];
        boolean[] squareSearchResults = new boolean[tests];
        long bruteForceTime = System.nanoTime();
        BruteForcePolygonSifterImpl bruteForce = new BruteForcePolygonSifterImpl(polygons);
        for (int i = 0; i < tests; i++) {
            bruteForceResults[i] = bruteForce.contains(points[i]);
        }
        bruteForceTime = System.nanoTime() - bruteForceTime;
        long squareSearchTime = System.nanoTime();
        SquareSearchPolygonSifterImpl squareSearch = new SquareSearchPolygonSifterImpl(polygons, tests);
        for (int i = 0; i < tests; i++) {
            squareSearchResults[i] = squareSearch.contains(points[i]);
        }
        squareSearchTime = System.nanoTime() - squareSearchTime;
        LOG.info("Brute Force:   {}ns", bruteForceTime);
        LOG.info("Square Search: {}ns", squareSearchTime);
        for (int i = 0; i < tests; i++) {
            assertEquals(points[i].toString(), bruteForceResults[i], squareSearchResults[i]);
        }
    }



}
