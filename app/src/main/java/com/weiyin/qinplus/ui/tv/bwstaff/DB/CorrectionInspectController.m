//
//  CorrectionInspectController.m
//  BWScore
//
//  Created by GatsWang on 22/08/16.
//  Copyright © 2016年 陈世哲. All rights reserved.
//
#import "CorrectionInspectController.h"
#pragma mark - Convience
const static int CORRECT_DURATION_IN_TICK = 50;
typedef struct NoteCache {
    int pitch;
    int tick;
    int velocity;
} NoteCache;
@implementation CorrectionInspectController {
    BOOL _in;
    unsigned int _slotIdx;
    NSMutableArray* _note;
    double _result1;
    NSMutableArray* _result2;
    NSMutableArray* _result3;
    unsigned int correct_offset;
}
-(instancetype)initWithDelegate:(id<CorrectionInspectControllerDelegate>)del {
    if (!del) {
        return nil;
    }
    if (self = [super init]) {
        _delegate = del;
        _note = [NSMutableArray array];
        _result1 = 0;
        _result2 = [NSMutableArray array];
        _result3 = [NSMutableArray array];
        _in = NO;
        _slotIdx = 0;
        [self setOffset];
    }
    return self;
}
- (instancetype)init {return nil;}
- (void)noteOn:(int)key velocity:(int)velocity tick:(int)tick {
    
    if (_in && (velocity != 0)) {
        NSLog(@"键盘成功输入ipad tick为:%d",tick);
//        [[NSNotificationCenter defaultCenter]postNotificationName:@"userIn" object:[NSString stringWithFormat:@"%d",key] userInfo:nil];
        NoteCache* note = (NoteCache*)malloc(sizeof(NoteCache));
//        note->pitch = key+1;
        note->pitch = key;
        note->tick = tick;
        note->velocity = velocity;
        [_note addObject:[NSValue valueWithPointer:note]];
    }else{
        if (!_in) {
            NSLog(@"按键的时间不对");
        }
    }
}
- (void)tick:(int)tick
{
    //HF 改动    unsigned int maxAmount = _score->hasZeroMeasure ? _score->slotAmount - 1 : _score->slotAmount; 考级4级中的《A大调回旋曲》最后一个音没有勾圈叉
    unsigned int maxAmount = _score->hasZeroMeasure ? _score->slotAmount  : _score->slotAmount;
    if (_in && (tick >= _score->slot[_slotIdx].tick + CORRECT_DURATION_IN_TICK))
    {   //当当前的tick值大于当前的音符的tick值 + 50后和进入计算
        [self inspect];
        //计算完后切换下一个音符
        _slotIdx ++;

        //设置是否可进入（用户在钢琴上输入的内容可记录）
        if (tick < _score->slot[_slotIdx].tick - CORRECT_DURATION_IN_TICK || _slotIdx == maxAmount) {
            _in = NO;
            ///HF 调试用的
//            [[NSNotificationCenter defaultCenter] postNotificationName:@"deleShwo" object:nil];
            [self freeCache];
        }
    } else if (_slotIdx != maxAmount
               && (tick >= _score->slot[_slotIdx].tick - CORRECT_DURATION_IN_TICK))
    {   //当前的tick值 大于当前音符的对应的tick值-50 可进入（用户在钢琴上输入的内容可记录）
        _in = YES;
        ///HF 调试用的
//        NSString * str = @"";
//        for (int index = 0; index!= 12; index++) {
//            if (_score->slot[_slotIdx].note[index].pitch != 0) {
//                str = [NSString stringWithFormat:@"%@,%d",str,_score->slot[_slotIdx].note[index].pitch];
//            }
//        }
//        [[NSNotificationCenter defaultCenter] postNotificationName:@"showIndex" object:str];
    }
}
- (void)setSlotIdx:(unsigned int)idx {
    _slotIdx = idx;
}
- (void)freeCache {
    for (id note in _note) {
        free((NoteCache*)[note pointerValue]);
    }
    [_note removeAllObjects];
}
- (NSArray*)end{
    if (_in && _note.count) {
        [self inspect];
    }
    _in = NO;
    _slotIdx = 0;
    NSArray* result = [self result];
    [self freeCache];
    _result1 = 0;
    [_result2 removeAllObjects];
    [_result3 removeAllObjects];
    return result;
}
- (void)inspect {
    ///HF 计算结果
    SZSlotBlock slot = _score->slot[_slotIdx];
    double tickResult = 0;
    double velocityResult = 0;
    unsigned int correctAmount = 0;
    for (unsigned int note_i = 0; note_i < slot.noteAmount; note_i++) {
        unsigned int pitch = slot.note[note_i].pitch;
        BOOL found = NO;
        unsigned int i = 0, l = (unsigned int)_note.count;
        for (; i < l; i++) {
            NoteCache nc = *(NoteCache*)[_note[i] pointerValue];
            if (pitch == nc.pitch) {
                found = YES;
                tickResult += ABS(nc.tick - slot.tick);
                velocityResult += nc.velocity;
                break;
            }
        }
        if (found) {
            correctAmount ++;
            [_note removeObjectAtIndex:i];
        }
    }
    unsigned int st = _slotIdx < _score->slotAmount ? _score->slot[_slotIdx+1].tick - CORRECT_DURATION_IN_TICK : UINT_MAX;
    unsigned int wrongAmount = 0;
    NSMutableIndexSet* is = [NSMutableIndexSet indexSet];
    for (unsigned int i = 0, l = (unsigned int)_note.count; i < l; i++) {
        int at = ((NoteCache*)[_note[i] pointerValue])->tick;
        if (at < st) {
            wrongAmount ++;
            [is addIndex:i];
        }
    }
    [_note removeObjectsAtIndexes:is];
    unsigned int correctCorrectAmount = 0;
    for (int i = 0; i < slot.noteAmount; i++) {
        if (slot.note[i].pitch != 0) {
            correctCorrectAmount ++;
        }
    }
    //有错即为错
    if (wrongAmount != 0) {
        ///HF 错
        NSLog(@"标记错误   %d",_slotIdx);
        [_delegate slot:_slotIdx correct:1];
    } else if (correctAmount != correctCorrectAmount) {
        //在没有错的情况下正确的数量 不对为缺失
        //HF 缺失
        NSLog(@"标记缺失  %d",_slotIdx);
        
        [_delegate slot:_slotIdx correct:2];
    } else {
        //对
        NSLog(@"标记对   %d",_slotIdx);
        _result1 ++;
        if (correctAmount) {
            //当前组音符的tick偏差和 ／ 当前组音符的数量
            if (tickResult <= 20 * correctAmount) {
                [_result2 addObject:@(0 / correctAmount)];
            }else{
                [_result2 addObject:@(tickResult / correctAmount)];
            }
            [_result3 addObject:@(velocityResult / correctAmount)];
            ///HF 对
            [_delegate slot:_slotIdx correct:0];
        }
    }
}
//HF 韩艳锋的标记 这bug我也是醉了
-(void)setOffset{
    //    NSDateFormatter *ScoreLength=[[NSDateFormatter alloc] init];
    //    [ScoreLength setDateStyle:NSDateFormatterShortStyle];
    //    [ScoreLength setDateFormat:@"YYYYMMDD"];
    //    NSString *scoreTick = [[ScoreLength stringFromDate:[NSDate date]] substringFromIndex:5];
    //    if ([scoreTick intValue]/100>=5) {
    //        correct_offset = 1;
    //    }else
    correct_offset =0;
}
- (NSArray *)result {
    double result1 = _result1 / _score->slotAmount;
    double result2 = 0;
    double result3 = 0;
    double resultAll = 0;
    NSUInteger l = _result2.count;
    if (l != 0) {
        for (NSNumber* tickOffset in _result2) {
            double oneResult = 1.0f - (1.0f / CORRECT_DURATION_IN_TICK) * [tickOffset doubleValue];
            result2 += (oneResult / l);
        }
    }
    l = _result3.count;
    if (l != 0) {
        for (NSNumber* velocity in _result3) {
            int vel = [velocity intValue];
            if (vel >= 20 && vel < 127) {
                result3 += (1.0f / l);
            }
        }
    }
//    result2 += 0.05; result2 = result2 * result1;
//    result3 -= 0.05; result3 = result3 * result1;
    resultAll = result1 * 0.5 + result2 * 0.3 + result3 * 0.2;
    return @[@(resultAll), @(result1), @(result2), @(result3)];
}
@end
